package com.ymware.engine.model.request;

import java.util.HashMap;
import java.util.Map;

/**
 * 带有上下文的模板请求
 */
public class ContextTemplateRequest {

    /**
     * 请求参数
     */
    private ExpressionBaseRequest request;

    /**
     * 上下文参数
     */
    private Map<String, Object> envContext = new HashMap<>();

    /**
     * 缓存参数
     */
    private Map<String, Object> cache = new HashMap<>();


    public Map<String, Object> getEnvContext() {
        return envContext;
    }

    public void setEnvContext(Map<String, Object> envContext) {
        this.envContext = envContext;
    }

    public Map<String, Object> getCache() {
        return cache;
    }

    public void setCache(Map<String, Object> cache) {
        this.cache = cache;
    }

    public ExpressionBaseRequest getRequest() {
        return request;
    }

    public void setRequest(ExpressionBaseRequest request) {
        this.request = request;
    }
}
