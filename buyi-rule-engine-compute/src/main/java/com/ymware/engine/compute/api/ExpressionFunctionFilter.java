package com.ymware.engine.compute.api;

import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.process.FunctionFilterChain;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.model.ExpressionConfigTreeModel;
import com.ymware.engine.model.FunctionApiModel;

import java.util.List;

/**
 * 表达式函数过滤
 */
public interface ExpressionFunctionFilter {

    Object doFunctionFilter(ExpressionEnvContext env, ExpressionConfigTreeModel configTreeModel,
                            ExpressionBaseRequest request, FunctionApiModel functionInfo,
                            List<Object> funcArgs, FunctionFilterChain chain);

}
