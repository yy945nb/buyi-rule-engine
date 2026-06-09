package com.ymware.engine.compute.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.ymware.engine.domain.value.service.ValueResolve;
import com.ymware.engine.compute.service.VariableResolveService;
import com.ymware.engine.entity.RuleEngineFunction;
import com.ymware.engine.entity.RuleEngineFunctionValue;
import com.ymware.engine.entity.RuleEngineInputParameter;
import com.ymware.engine.entity.RuleEngineVariable;
import com.ymware.engine.service.RuleEngineFunctionManager;
import com.ymware.engine.service.RuleEngineFunctionValueManager;
import com.ymware.engine.service.RuleEngineInputParameterManager;
import com.ymware.engine.service.RuleEngineVariableManager;
import com.ymware.engine.domain.value.model.Function;
import com.ymware.engine.domain.value.model.Value;
import com.ymware.engine.domain.value.model.ValueType;
import com.ymware.engine.domain.value.model.VariableType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/7/17
 * @since 1.0.0
 */
@Slf4j
@Service
public class VariableResolveServiceImpl implements VariableResolveService {

    @Resource
    private RuleEngineVariableManager ruleEngineVariableManager;
    @Resource
    private RuleEngineFunctionValueManager ruleEngineFunctionValueManager;
    @Resource
    private RuleEngineFunctionManager ruleEngineFunctionManager;
    @Resource
    private ValueResolve valueResolve;
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private RuleEngineInputParameterManager ruleEngineInputParameterManager;

    /**
     * 获取所有的变量/函数配置信息
     *
     * @return 变量
     */
    @Override
    public Map<Long, Value> getAllVariable() {
        log.info("开始加载规则引擎变量");
        Map<Long, Value> maps = new HashMap<>(100);
        // 查询到所有的变量 待优化
        List<RuleEngineVariable> engineVariables = this.ruleEngineVariableManager.list();
        List<RuleEngineFunction> engineFunctions = this.ruleEngineFunctionManager.list();
        Map<Long, RuleEngineFunction> engineFunctionMap = engineFunctions.stream().collect(Collectors.toMap(RuleEngineFunction::getId, java.util.function.Function.identity()));
        List<RuleEngineFunctionValue> engineFunctionValues = this.ruleEngineFunctionValueManager.list();
        Map<Long, List<RuleEngineFunctionValue>> functionValueMap = engineFunctionValues.stream().collect(Collectors.groupingBy(RuleEngineFunctionValue::getVariableId));
        List<RuleEngineInputParameter> inputParameters = this.ruleEngineInputParameterManager.list();
        Map<Long, RuleEngineInputParameter> inputParameterMap = inputParameters.stream().collect(Collectors.toMap(RuleEngineInputParameter::getId, java.util.function.Function.identity()));
        for (RuleEngineVariable engineVariable : engineVariables) {
            try {
                Integer type = engineVariable.getType();
                if (VariableType.CONSTANT.getType().equals(type)) {
                    // 固定值变量
                    Value value = this.valueResolve.getValue(VariableType.CONSTANT.getType(), engineVariable.getValueType(), engineVariable.getValue(), inputParameterMap);
                    maps.put(engineVariable.getId(), value);
                } else if (VariableType.FUNCTION.getType().equals(type)) {
                    // 函数变量
                    maps.put(engineVariable.getId(), this.functionProcess(engineVariable, engineFunctionMap, functionValueMap, inputParameterMap));
                } else if (VariableType.FORMULA.getType().equals(type)) {
                    // 表达式变量
                    Value value = this.valueResolve.getValue(type, engineVariable.getValueType(), engineVariable.getValue(), inputParameterMap);
                    maps.put(engineVariable.getId(), value);
                }
            } catch (Exception e) {
                log.warn("加载变量失败，变量Id：{}", engineVariable.getId(), e);
            }
        }
        return maps;
    }


    /**
     * 根据变量获取变量/函数配置信息
     *
     * @param id 变量id
     * @return 变量
     */
    @Override
    public Value getVarById(Long id) {
        RuleEngineVariable ruleEngineVariable = this.ruleEngineVariableManager.getById(id);
        // 固定值变量
        if (Objects.equals(ruleEngineVariable.getType(), VariableType.CONSTANT.getType())) {
            return this.valueResolve.getValue(VariableType.CONSTANT.getType(), ruleEngineVariable.getValueType(), ruleEngineVariable.getValue());
        } else if (Objects.equals(ruleEngineVariable.getType(), VariableType.FORMULA.getType())) {
            // 表达式变量
            return this.valueResolve.getValue(VariableType.FORMULA.getType(), ruleEngineVariable.getValueType(), ruleEngineVariable.getValue());
        }
        // 函数变量
        RuleEngineFunction engineFunction = this.ruleEngineFunctionManager.getById(ruleEngineVariable.getValue());
        List<RuleEngineFunctionValue> functionValueList = this.ruleEngineFunctionValueManager.lambdaQuery()
                .eq(RuleEngineFunctionValue::getFunctionId, ruleEngineVariable.getValue())
                .eq(RuleEngineFunctionValue::getVariableId, ruleEngineVariable.getId()).list();

        Map<String, Value> param = new HashMap<>(10);
        if (CollUtil.isNotEmpty(functionValueList)) {
            for (RuleEngineFunctionValue engineFunctionValue : functionValueList) {
                param.put(engineFunctionValue.getParamCode(), this.valueResolve.getValue(engineFunctionValue.getType(), engineFunctionValue.getValueType(), engineFunctionValue.getValue()));
            }
        }
        Object abstractFunction = this.applicationContext.getBean(engineFunction.getExecutor());
        return new Function(engineFunction.getId(), abstractFunction, ValueType.getByValue(engineFunction.getReturnValueType()), param);

    }


    /**
     * 规则引擎函数处理
     *
     * @param ruleEngineVariable 规则函数元数据
     * @param engineFunctionMap  函数缓存数据
     * @return Function
     */
    private Function functionProcess(RuleEngineVariable ruleEngineVariable, Map<Long, RuleEngineFunction> engineFunctionMap, Map<Long, List<RuleEngineFunctionValue>> functionValueMap, Map<Long, RuleEngineInputParameter> inputParameterMap) {
        Long functionId = Long.valueOf(ruleEngineVariable.getValue());
        RuleEngineFunction engineFunction = engineFunctionMap.get(functionId);

        List<RuleEngineFunctionValue> functionValueList = functionValueMap.get(ruleEngineVariable.getId());
        Map<String, Value> param = new HashMap<>(10);
        if (CollUtil.isNotEmpty(functionValueList)) {
            for (RuleEngineFunctionValue engineFunctionValue : functionValueList) {
                Value value = this.valueResolve.getValue(engineFunctionValue.getType(), engineFunctionValue.getValueType(), engineFunctionValue.getValue(), inputParameterMap);
                param.put(engineFunctionValue.getParamCode(), value);
            }
        }
        Object abstractFunction = this.applicationContext.getBean(engineFunction.getExecutor());
        return new Function(engineFunction.getId(), abstractFunction, ValueType.getByValue(engineFunction.getReturnValueType()), param);
    }

}
