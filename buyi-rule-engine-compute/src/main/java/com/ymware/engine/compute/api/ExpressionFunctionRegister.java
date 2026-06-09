package com.ymware.engine.compute.api;


import com.ymware.engine.model.request.ContextTemplateRequest;

import java.util.Map;

public interface ExpressionFunctionRegister {

    /**
     * 是否匹配
     *
     * @param groupName
     * @return
     */
    default boolean isMatch(String groupName) {
        return true;
    }

    /**
     * 查找函数对象
     *
     * @param functionName 函数名称
     * @return
     */
    public boolean finderFunction(String functionName);

    /**
     * 函数执行
     *
     * @param functionName 函数名称
     * @param env          环境变量
     * @param request      请求参数
     * @return
     */
    public Object call(String functionName, ContextTemplateRequest env, Map<String, Object> request) throws Exception;

}
