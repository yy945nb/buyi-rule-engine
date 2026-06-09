package com.ymware.engine.compute.api;

import java.util.concurrent.ExecutorService;

/**
 * 异步表达式执行器
 */
public interface ExpressionAsyncThreadExecutor {

    /**
     * 异步执行
     */
    ExecutorService executorService();
}
