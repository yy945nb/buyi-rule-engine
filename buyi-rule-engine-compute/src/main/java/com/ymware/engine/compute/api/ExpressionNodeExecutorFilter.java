package com.ymware.engine.compute.api;

import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.process.ExpressionNodeFilterChain;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.result.ExpressionConfigInfo;
import com.ymware.engine.model.ExpressionConfigTreeModel;

/**
 * 表达式子节点执行过滤器
 *
 * @author liukaixiong
 * @date 2024/8/23 - 10:10
 */
public interface ExpressionNodeExecutorFilter {

    void doExpressionNodeFilter(ExpressionBaseRequest baseRequest, ExpressionEnvContext envContext,
                                ExpressionConfigInfo configInfo, ExpressionConfigTreeModel configTreeModelList,
                                Object execute, ExpressionNodeFilterChain chain);

}
