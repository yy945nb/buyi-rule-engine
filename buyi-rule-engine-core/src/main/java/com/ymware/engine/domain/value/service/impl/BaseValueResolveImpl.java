package com.ymware.engine.domain.value.service.impl;

import com.ymware.engine.domain.value.service.ValueResolve;
import com.ymware.engine.domain.value.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import cn.hutool.core.bean.BeanUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 值解析服务基础实现
 * 处理 4 种基础类型的值解析 (INPUT_PARAMETER, VARIABLE, CONSTANT, FORMULA)
 * GENERAL_RULE 类型由 web 模块扩展实现
 */
@Slf4j
@Component
public class BaseValueResolveImpl implements ValueResolve {

    @Override
    public Value getValue(Integer type, String valueType, String value, Map<Long, ?> engineInputParameterMap) {
        VariableType variableTypeEnum = VariableType.getByType(type);
        ValueType valueTypeObj = ValueType.getByValue(valueType);
        switch (variableTypeEnum) {
            case INPUT_PARAMETER:
                Object parameterObj = engineInputParameterMap.get(Long.valueOf(value));
                if (parameterObj == null) {
                    log.warn("输入参数不存在: {}", value);
                    return null;
                }
                Long id = (Long) BeanUtil.getFieldValue(parameterObj, "id");
                String code = (String) BeanUtil.getFieldValue(parameterObj, "code");
                return new InputParameter(id, code, valueTypeObj);
            case VARIABLE:
                return new Variable(Long.valueOf(value), valueTypeObj);
            case CONSTANT:
                return new Constant(value, valueTypeObj);
            case FORMULA:
                return new Formula(value, valueTypeObj);
            default:
                log.warn("基础实现不支持的值类型: {}，请使用 web/compute 模块的扩展实现", type);
                return null;
        }
    }

    @Override
    public Value getValue(Integer type, String valueType, String value) {
        return getValue(type, valueType, value, new HashMap<>());
    }
}
