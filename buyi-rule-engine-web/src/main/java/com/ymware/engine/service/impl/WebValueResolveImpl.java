package com.ymware.engine.service.impl;

import cn.hutool.core.lang.Validator;
import com.ymware.engine.domain.value.service.impl.BaseValueResolveImpl;
import com.ymware.engine.domain.value.model.*;
import com.ymware.engine.common.exception.ApiException;
import com.ymware.engine.service.WebValueResolve;
import com.ymware.engine.entity.RuleEngineGeneralRule;
import com.ymware.engine.entity.RuleEngineInputParameter;
import com.ymware.engine.entity.RuleEngineVariable;
import com.ymware.engine.store.manager.RuleEngineGeneralRuleManager;
import com.ymware.engine.service.RuleEngineInputParameterManager;
import com.ymware.engine.service.RuleEngineVariableManager;
import com.ymware.engine.vo.condition.ConfigValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * Web 模块值解析服务实现
 * 继承 common 模块基础实现，添加 ConfigValue 相关方法
 */
@Slf4j
@Component
public class WebValueResolveImpl extends BaseValueResolveImpl implements WebValueResolve {

    @Resource
    private RuleEngineInputParameterManager ruleEngineInputParameterManager;
    @Resource
    private RuleEngineVariableManager ruleEngineVariableManager;
    @Resource
    private RuleEngineGeneralRuleManager ruleEngineGeneralRuleManager;

    /**
     * 解析值/变量/规则参数/固定值
     */
    @Override
    public ConfigValue getConfigValue(Value cValue) {
        ConfigValue value = new ConfigValue();
        value.setValueType(cValue.getValueType().name());
        if (cValue instanceof Constant) {
            value.setType(VariableType.CONSTANT.getType());
            Constant constant = (Constant) cValue;
            value.setValue(String.valueOf(constant.getValue()));
            value.setValueName(String.valueOf(constant.getValue()));
        } else if (cValue instanceof InputParameter) {
            value.setType(VariableType.INPUT_PARAMETER.getType());
            InputParameter inputParameter = (InputParameter) cValue;
            RuleEngineInputParameter ruleEngineInputParameter = this.ruleEngineInputParameterManager.getById(inputParameter.getInputParameterId());
            if (ruleEngineInputParameter == null) {
                throw new ApiException("缺失参数：" + inputParameter.getInputParameterId());
            }
            value.setValue(String.valueOf(inputParameter.getInputParameterId()));
            value.setValueName(ruleEngineInputParameter.getName());
        } else if (cValue instanceof Variable) {
            value.setType(VariableType.VARIABLE.getType());
            Variable variable = (Variable) cValue;
            value.setValue(String.valueOf(variable.getVariableId()));
            RuleEngineVariable engineVariable = this.ruleEngineVariableManager.getById(variable.getVariableId());
            if (engineVariable == null) {
                throw new ApiException("缺失变量：" + variable.getVariableId());
            }
            value.setVariableType(engineVariable.getType());
            value.setValueName(engineVariable.getName());
            if (engineVariable.getType().equals(VariableType.CONSTANT.getType())) {
                value.setVariableValue(engineVariable.getValue());
            } else if (engineVariable.getType().equals(VariableType.FORMULA.getType())) {
                // 表达式配置
                value.setVariableValue(engineVariable.getValue());
            }
        } else if (cValue instanceof Executor) {
            value.setType(VariableType.GENERAL_RULE.getType());
            Executor executor = (Executor) cValue;
            RuleEngineGeneralRule ruleEngineGeneralRule = this.ruleEngineGeneralRuleManager.getById(executor.getId());
            if (ruleEngineGeneralRule == null) {
                throw new ApiException("缺失普通规则：" + executor.getId());
            }
            value.setValueName(ruleEngineGeneralRule.getName());
        }
        return value;
    }

    /**
     * 解析值/变量/规则参数/固定值
     */
    @Override
    public ConfigValue getConfigValue(String value, Integer type, String valueType) {
        ConfigValue configValue = new ConfigValue();
        if (Validator.isEmpty(type)) {
            return configValue;
        }
        configValue.setValueType(valueType);
        configValue.setType(type);
        if (Validator.isEmpty(value)) {
            return configValue;
        }
        if (type.equals(VariableType.INPUT_PARAMETER.getType())) {
            RuleEngineInputParameter engineInputParameter = this.ruleEngineInputParameterManager.getById(value);
            if (engineInputParameter == null) {
                throw new ApiException("缺失参数：" + value);
            }
            configValue.setValueName(engineInputParameter.getName());
        } else if (type.equals(VariableType.VARIABLE.getType())) {
            RuleEngineVariable engineVariable = this.ruleEngineVariableManager.getById(value);
            if (engineVariable == null) {
                throw new ApiException("缺失变量：" + value);
            }
            configValue.setValueName(engineVariable.getName());
            configValue.setVariableType(engineVariable.getType());
            if (engineVariable.getType().equals(VariableType.CONSTANT.getType())) {
                configValue.setVariableValue(engineVariable.getValue());
            } else if (engineVariable.getType().equals(VariableType.FORMULA.getType())) {
                // 表达式配置
                configValue.setVariableValue(engineVariable.getValue());
            }
        } else if (type.equals(VariableType.GENERAL_RULE.getType())) {
            RuleEngineGeneralRule ruleEngineGeneralRule = this.ruleEngineGeneralRuleManager.getById(value);
            if (ruleEngineGeneralRule == null) {
                throw new ApiException("缺失普通规则：" + value);
            }
            configValue.setValueName(ruleEngineGeneralRule.getName());
        }
        configValue.setValue(value);
        return configValue;
    }

    /**
     * 如果是变量，查询到变量name，如果是规则参数查询到规则参数name
     */
    @Override
    public ConfigValue getConfigValue(String value, Integer type, String valueType, Map<Long, RuleEngineVariable> variableMap, Map<Long, RuleEngineInputParameter> ruleEngineInputParameterMap) {
        String valueName = value;
        String variableValue = null;
        Integer variableType = null;
        if (type.equals(VariableType.INPUT_PARAMETER.getType())) {
            valueName = ruleEngineInputParameterMap.get(Long.valueOf(value)).getName();
        } else if (type.equals(VariableType.VARIABLE.getType())) {
            RuleEngineVariable engineVariable = variableMap.get(Long.valueOf(value));
            if (engineVariable == null) {
                throw new ApiException("缺失变量：" + value);
            }
            variableType = engineVariable.getType();
            valueName = engineVariable.getName();
            if (engineVariable.getType().equals(VariableType.CONSTANT.getType())) {
                variableValue = engineVariable.getValue();
            } else if (engineVariable.getType().equals(VariableType.FORMULA.getType())) {
                // 表达式配置
                variableValue = engineVariable.getValue();
            }
        } else if (type.equals(VariableType.GENERAL_RULE.getType())) {
            RuleEngineGeneralRule ruleEngineGeneralRule = this.ruleEngineGeneralRuleManager.getById(value);
            if (ruleEngineGeneralRule == null) {
                throw new ApiException("缺失普通规则：" + value);
            }
            valueName = ruleEngineGeneralRule.getName();
        }
        ConfigValue configBeanValue = new ConfigValue();
        configBeanValue.setType(type);
        configBeanValue.setValue(value);
        configBeanValue.setValueName(valueName);
        configBeanValue.setVariableValue(variableValue);
        configBeanValue.setValueType(valueType);
        configBeanValue.setVariableType(variableType);
        return configBeanValue;
    }
}
