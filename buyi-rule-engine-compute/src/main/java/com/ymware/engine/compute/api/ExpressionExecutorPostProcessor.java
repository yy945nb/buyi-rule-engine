package com.ymware.engine.compute.api;

import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.result.ExpressionConfigInfo;

/**
 * 执行器拦截处理器
 */
public interface ExpressionExecutorPostProcessor {


    /**
     * 前置执行器
     *
     * @param envContext
     * @param baseRequest
     * @param configInfo
     */
    void beforeExecutor(ExpressionEnvContext envContext, ExpressionBaseRequest baseRequest,
                        ExpressionConfigInfo configInfo);


    /**
     * 后置执行器
     *
     * @param envContext
     * @param baseRequest
     * @param configTreeModelList
     */
    void afterExecutor(ExpressionEnvContext envContext, ExpressionBaseRequest baseRequest,
                       ExpressionConfigInfo configTreeModelList);

}
