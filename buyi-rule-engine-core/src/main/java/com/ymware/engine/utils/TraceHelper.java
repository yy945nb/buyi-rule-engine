package com.ymware.engine.utils;

import org.slf4j.MDC;

/**
 * Trace helper for request ID management
 */
public class TraceHelper {

    public static final String REQUEST_ID = "requestId";

    public static String getRequestId() {
        return MDC.get(REQUEST_ID);
    }

    public static void setRequestId(String requestId) {
        MDC.put(REQUEST_ID, requestId);
    }

    public static void clear() {
        MDC.remove(REQUEST_ID);
    }
}
