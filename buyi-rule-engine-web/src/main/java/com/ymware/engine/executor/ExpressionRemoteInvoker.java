package com.ymware.engine.executor;

import com.ymware.engine.result.ApiResult;
import com.ymware.engine.model.RemoteExpressionModel;
import com.ymware.engine.enums.RemoteInvokeTypeEnums;
import com.ymware.engine.model.node.NodeServiceDto;

/**
 * 调用远端执行器定义
 */
public interface ExpressionRemoteInvoker {

    /**
     * 执行类型
     *
     * @return
     */
    public RemoteInvokeTypeEnums type();

    default String parseDomain(NodeServiceDto domain) {
        return domain.getDomain();
    }

    /**
     * 具体的执行方式
     *
     * @param url
     * @param expressionModel
     * @return
     * @throws Exception
     */
    public ApiResult<String> invoke(String url, RemoteExpressionModel expressionModel) throws Exception;
}
