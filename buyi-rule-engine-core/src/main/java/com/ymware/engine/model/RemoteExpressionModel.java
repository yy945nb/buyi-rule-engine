package com.ymware.engine.model;

import com.ymware.engine.model.request.RemoteExecutorRequest;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.enums.ExpressionKeyTypeEnums;

import java.util.List;

/**
 * 远程表达式执行模型
 */
public class RemoteExpressionModel {

    /**
     * 请求参数
     */
    private ExpressionBaseRequest request;

    /**
     * 执行类型
     */
    private ExpressionKeyTypeEnums keyType;

    /**
     * 执行变量或者函数的请求参数
     */
    private List<RemoteExecutorRequest> executorRequests;

    public ExpressionBaseRequest getRequest() {
        return request;
    }

    public void setRequest(ExpressionBaseRequest request) {
        this.request = request;
    }

    public ExpressionKeyTypeEnums getKeyType() {
        return keyType;
    }

    public void setKeyType(ExpressionKeyTypeEnums keyType) {
        this.keyType = keyType;
    }

    public List<RemoteExecutorRequest> getExecutorRequests() {
        return executorRequests;
    }

    public void setExecutorRequests(List<RemoteExecutorRequest> executorRequests) {
        this.executorRequests = executorRequests;
    }
}
