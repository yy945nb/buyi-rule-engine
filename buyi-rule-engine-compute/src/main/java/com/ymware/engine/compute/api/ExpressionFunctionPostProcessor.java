package com.ymware.engine.compute.api;

import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.model.ExpressionConfigTreeModel;
import com.ymware.engine.model.FunctionApiModel;

import java.util.List;

/**
 * 表达式函数拦截后置处理器
 */
public interface ExpressionFunctionPostProcessor {

    /**
     * 函数执行之前触发
     *
     * @param env             上下文环境变量
     * @param configTreeModel 表达式配置对象
     * @param request         请求入参模型
     * @param functionInfo    函数信息
     * @param funcArgs        函数参数
     */
    default void functionBefore(ExpressionEnvContext env, ExpressionConfigTreeModel configTreeModel,
                                ExpressionBaseRequest request, FunctionApiModel functionInfo, List<Object> funcArgs) {

    }

    /**
     * 函数表达式执行完成之后
     *
     * @param env             上下文环境变量
     * @param configTreeModel 表达式配置对象
     * @param request         请求入参模型
     * @param functionInfo    函数信息
     * @param funcArgs        函数参数
     * @param result          函数执行结果
     */
    default void afterFunction(ExpressionEnvContext env, ExpressionConfigTreeModel configTreeModel,
                               ExpressionBaseRequest request, FunctionApiModel functionInfo, List<Object> funcArgs, Object result) {

    }

    /**
     * 函数执行异常
     *
     * @param env             上下文环境变量
     * @param configTreeModel 表达式配置对象
     * @param request         请求入参模型
     * @param functionInfo    函数信息
     * @param funcArgs        函数参数
     * @param e               异常信息
     */
    default void functionError(ExpressionEnvContext env, ExpressionConfigTreeModel configTreeModel,
                               ExpressionBaseRequest request, FunctionApiModel functionInfo, List<Object> funcArgs, Exception e) {

    }

}
