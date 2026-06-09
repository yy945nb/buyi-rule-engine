package com.ymware.engine.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 链路追踪拦截器
 */
@Slf4j
@Component
public class TraceInterceptor implements HandlerInterceptor {

    public final static String REQUEST_ID = "requestId";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 如果上游系统传入requestId则使用上游系统的requestId
        String requestId = request.getHeader(REQUEST_ID);
        if (StringUtils.isEmpty(requestId)) {
            // 否则生成一个
            requestId = UUID.randomUUID().toString();
        }
        MDC.put(REQUEST_ID, requestId);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception exception) {
        MDC.clear();
    }

    /**
     * 获取request id
     *
     * @return request id
     */
    public static String getRequestId() {
        return MDC.get(TraceInterceptor.REQUEST_ID);
    }

}
