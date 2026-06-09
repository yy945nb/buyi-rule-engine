package com.ymware.engine.compute.log;

import com.ymware.engine.model.request.ExpressionBaseRequest;

/**
 * @author liukaixiong
 * @date 2023/12/22
 */
public interface LogTraceService {


    /**
     * 日志规范追踪
     *
     * @param request      请求入参
     * @param logEventType 关键事件
     * @param text         日志描述
     * @param logParam     日志参数
     */
    void trace(ExpressionBaseRequest request, LogEventEnum logEventType, String text, Object... logParam);


}
