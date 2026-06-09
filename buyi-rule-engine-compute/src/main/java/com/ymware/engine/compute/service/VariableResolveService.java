package com.ymware.engine.compute.service;

import com.ymware.engine.domain.value.model.Value;

import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/7/17
 * @since 1.0.0
 */
public interface VariableResolveService {

    /**
     * 获取所有的变量/函数配置信息
     *
     * @return 变量
     */
    Map<Long, Value> getAllVariable();

    /**
     * 根据变量获取变量/函数配置信息
     *
     * @param id 变量id
     * @return 变量
     */
    Value getVarById(Long id);

}
