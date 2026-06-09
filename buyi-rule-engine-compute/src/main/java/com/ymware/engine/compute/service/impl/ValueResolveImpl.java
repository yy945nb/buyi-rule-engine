package com.ymware.engine.compute.service.impl;

import com.ymware.engine.domain.value.model.Value;
import com.ymware.engine.domain.value.model.ValueType;
import com.ymware.engine.domain.value.model.VariableType;
import com.ymware.engine.domain.value.model.InputParameter;
import com.ymware.engine.domain.value.service.ValueResolve;
import com.ymware.engine.domain.value.service.impl.BaseValueResolveImpl;
import com.ymware.engine.entity.RuleEngineInputParameter;
import com.ymware.engine.service.RuleEngineInputParameterManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * Compute 模块值解析服务实现
 * 继承 core 模块基础实现，添加数据库查询相关逻辑
 */
@Slf4j
@Component
public class ValueResolveImpl extends BaseValueResolveImpl implements ValueResolve {

    @Resource
    private RuleEngineInputParameterManager ruleEngineInputParameterManager;

    @Override
    public Value getValue(Integer type, String valueTypeStr, String value) {
        VariableType variableTypeEnum = VariableType.getByType(type);
        if (variableTypeEnum == VariableType.INPUT_PARAMETER) {
            RuleEngineInputParameter ruleEngineInputParameter = this.ruleEngineInputParameterManager.getById(value);
            if (ruleEngineInputParameter == null) {
                log.warn("输入参数不存在: {}", value);
                return null;
            }
            ValueType valueType = ValueType.getByValue(valueTypeStr);
            return new InputParameter(ruleEngineInputParameter.getId(), ruleEngineInputParameter.getCode(), valueType);
        }
        return super.getValue(type, valueTypeStr, value);
    }
}
