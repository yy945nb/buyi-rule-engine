package com.ymware.engine.compute.service.impl;

import com.ymware.engine.common.enums.ErrorCodeEnum;
import com.ymware.engine.common.exception.ApiException;
import com.ymware.engine.compute.service.FunctionService;
import com.ymware.engine.entity.RuleEngineFunction;
import com.ymware.engine.service.RuleEngineFunctionManager;
import com.ymware.engine.domain.value.model.ExecuteFunctionRequest;
import com.ymware.engine.domain.value.model.ParamValue;
import com.ymware.engine.config.RuleEngineConfiguration;
import com.ymware.engine.domain.value.model.Constant;
import com.ymware.engine.domain.value.model.Function;
import com.ymware.engine.domain.value.model.Value;
import com.ymware.engine.domain.value.model.ValueType;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/8/27
 * @since 1.0.0
 */
@Service
public class ComputeFunctionServiceImpl implements FunctionService {

    @Resource
    private RuleEngineFunctionManager ruleEngineFunctionManager;
    @Resource
    private ApplicationContext applicationContext;

    /**
     * 函数模拟测试
     *
     * @param executeTestRequest 函数入参值
     * @return result
     */
    @Override
    public Object run(ExecuteFunctionRequest executeTestRequest) {
        Long functionId = executeTestRequest.getId();
        RuleEngineFunction engineFunction = this.ruleEngineFunctionManager.getById(functionId);
        if (engineFunction == null) {
            throw new ApiException(ErrorCodeEnum.RULE9999404.getCode(),"不存在函数：{}", functionId);
        }
        String executor = engineFunction.getExecutor();
        if (this.applicationContext.containsBean(executor)) {
            Object abstractFunction = this.applicationContext.getBean(executor);
            // 函数测试均为固定值
            List<ParamValue> paramValues = executeTestRequest.getParamValues();
            Map<String, Value> param = new HashMap<>(paramValues.size());
            for (ParamValue paramValue : paramValues) {
                Constant constant = new Constant(paramValue.getValue(), ValueType.getByValue(paramValue.getValueType()));
                param.put(paramValue.getCode(), constant);
            }
            Function function = new Function(functionId, abstractFunction, ValueType.STRING, param);
            // 无规则参数 input==null
            return function.getValue(null, new RuleEngineConfiguration());
        } else {
            throw new ApiException("容器中找不到{}函数", executor);
        }
    }

}
