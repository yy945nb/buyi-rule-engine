package com.ymware.engine.compute.service;

import com.ymware.engine.domain.value.model.ExecuteFunctionRequest;

/**
 * 〈FunctionService〉
 *
 * @author 丁乾文
 * @date 2021/6/17 10:18 下午
 * @since 1.0.0
 */
public interface FunctionService {

    /**
     * 函数模拟测试
     *
     * @param executeTestRequest 函数入参值
     * @return result
     */
    Object run(ExecuteFunctionRequest executeTestRequest);

}
