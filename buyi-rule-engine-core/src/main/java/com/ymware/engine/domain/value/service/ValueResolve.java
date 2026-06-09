package com.ymware.engine.domain.value.service;

import com.ymware.engine.domain.value.model.Value;
import java.util.Map;

/**
 * 值解析服务接口
 * 提供 getValue 方法用于解析不同类型的值
 */
public interface ValueResolve {

    /**
     * 获取值（带缓存map版本）
     *
     * @param type 值类型
     * @param valueType 值的具体类型
     * @param value 值
     * @param engineInputParameterMap 输入参数缓存 (值为实体对象)
     * @return 解析后的值
     */
    Value getValue(Integer type, String valueType, String value, Map<Long, ?> engineInputParameterMap);

    /**
     * 获取值（无缓存版本）
     *
     * @param type 值类型
     * @param valueType 值的具体类型
     * @param value 值
     * @return 解析后的值
     */
    Value getValue(Integer type, String valueType, String value);
}
