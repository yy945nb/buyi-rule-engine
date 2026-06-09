package com.ymware.engine.compute.api;

import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.model.ExpressionConfigTreeModel;

/**
 * 表达式类型回调
 */
public interface ExpressionConfigExecutorIntercept {

    default void before(ExpressionConfigTreeModel expressionType, ExpressionBaseRequest baseRequest, ExpressionEnvContext envContext) {

    }

    /**
     * 执行器回调
     *
     * @param expressionType 表达式对象
     * @param baseRequest    请求参数
     * @param envContext     上下文参数
     * @param execute        执行结果
     */
    default void after(ExpressionConfigTreeModel expressionType, ExpressionBaseRequest baseRequest, ExpressionEnvContext envContext, Object execute) {

    }


    /**
     * 执行器出现异常的情况
     *
     * @param expressionType
     * @param baseRequest
     * @param envContext
     * @param e
     */
    default void error(ExpressionConfigTreeModel expressionType, ExpressionBaseRequest baseRequest, ExpressionEnvContext envContext, Exception e) {

    }

}
