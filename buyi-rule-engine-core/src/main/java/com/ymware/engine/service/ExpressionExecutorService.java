package com.ymware.engine.service;

import com.ymware.engine.result.ExpressionConfigInfo;

import java.util.Map;

/**
 * 表达式执行
 */
public interface ExpressionExecutorService {

    /**
     * 执行表达式任务
     *
     * @param serviceName  服务名称
     * @param businessCode 业务编码
     * @param eventName    事件名称
     * @param unionId      唯一编号
     * @param traceId      追踪编号
     * @param request      请求模型
     * @return
     */
    public Map<String, Object> invoke(String serviceName, String businessCode, String eventName, String unionId, String traceId, Object request);

    public ExpressionConfigInfo queryBusinessConfigInfo(String serviceName, String businessCode, String executorCode);

    public ExpressionConfigInfo getRefreshBusinessConfigInfo(String serviceName, String businessCode, String executorCode);

    public ExpressionConfigInfo getBusinessConfigInfo(String serviceName, String businessCode, String executorCode);

}


