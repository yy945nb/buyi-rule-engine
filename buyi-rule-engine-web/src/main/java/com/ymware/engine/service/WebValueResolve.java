package com.ymware.engine.service;

import com.ymware.engine.domain.value.service.ValueResolve;
import com.ymware.engine.domain.value.model.Value;
import com.ymware.engine.entity.RuleEngineInputParameter;
import com.ymware.engine.entity.RuleEngineVariable;
import com.ymware.engine.vo.condition.ConfigValue;

import java.util.Map;

/**
 * Web 模块值解析服务接口
 * 继承 common 模块接口，添加 web 独有的 ConfigValue 方法
 */
public interface WebValueResolve extends ValueResolve {

    /**
     * 解析值/变量/规则参数/固定值
     *
     * @param cValue Value
     * @return ConfigBean.Value
     */
    ConfigValue getConfigValue(Value cValue);

    /**
     * 解析值/变量/规则参数/固定值
     *
     * @param value     结果值/可能为变量/规则参数
     * @param type      变量/规则参数/固定值
     * @param valueType STRING/NUMBER...
     * @return Action
     */
    ConfigValue getConfigValue(String value, Integer type, String valueType);

    /**
     * 如果是变量，查询到变量name，如果是规则参数查询到规则参数name
     *
     * @param type                    类型 变量/规则参数/固定值
     * @param value                   值
     * @param valueType               值类型 STRING/NUMBER...
     * @param variableMap             变量缓存
     * @param engineInputParameterMap 规则参数缓存
     * @return ConfigValue
     */
    ConfigValue getConfigValue(String value, Integer type, String valueType, Map<Long, RuleEngineVariable> variableMap, Map<Long, RuleEngineInputParameter> engineInputParameterMap);
}
