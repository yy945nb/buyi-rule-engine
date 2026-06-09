package com.ymware.engine.compute.process;

import com.ymware.engine.compute.api.ExpressionExecutorFilter;
import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.result.ExpressionConfigInfo;
import com.ymware.engine.model.ExpressionConfigTreeModel;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Supplier;

/**
 * 表达式过滤器责任链
 */
public class ExpressionFilterChain {

    /**
     * 拦截器
     */
    private final List<ExpressionExecutorFilter> expressionExecutorFilters;

    /**
     * 具体的执行方法
     */
    private final Supplier<Object> supplier;

    private int index = 0;

    public ExpressionFilterChain(List<ExpressionExecutorFilter> expressionExecutorFilters, Supplier<Object> supplier) {
        this.expressionExecutorFilters = expressionExecutorFilters;
        this.supplier = supplier;
    }


    public Object doFilter(ExpressionEnvContext env, ExpressionConfigInfo configInfo, ExpressionConfigTreeModel configTreeModel, ExpressionBaseRequest request) {

        if (CollectionUtils.isEmpty(expressionExecutorFilters) || expressionExecutorFilters.size() == index) {
            return supplier.get();
        }

        return expressionExecutorFilters.get(index++).doExpressionFilter(env, configInfo,configTreeModel, request, this);
    }

}
