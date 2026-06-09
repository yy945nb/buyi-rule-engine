package com.ymware.engine.compute.api;

import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.process.ExpressionFilterChain;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.result.ExpressionConfigInfo;
import com.ymware.engine.model.ExpressionConfigTreeModel;

/**
 * 表达式过滤链条
 */
public interface ExpressionExecutorFilter {

    Object doExpressionFilter(ExpressionEnvContext env, ExpressionConfigInfo configInfo, ExpressionConfigTreeModel configTreeModel, ExpressionBaseRequest request, ExpressionFilterChain chain);

}
