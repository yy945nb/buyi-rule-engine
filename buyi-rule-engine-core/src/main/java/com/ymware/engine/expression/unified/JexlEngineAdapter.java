package com.ymware.engine.expression.unified;

import com.ymware.engine.domain.rule.model.ExecutionContext;
import com.ymware.engine.domain.rule.model.ExpressionEvaluationException;
import com.ymware.engine.expression.ExpressionEvaluator;

/**
 * JEXL 引擎适配器 - 包装已有的 ExpressionEvaluator 实现
 */
public class JexlEngineAdapter implements ExpressionEngineAdapter {

    private final ExpressionEvaluator delegate;

    public JexlEngineAdapter(ExpressionEvaluator delegate) {
        this.delegate = delegate;
    }

    @Override
    public ExpressionEngineType getEngineType() {
        return ExpressionEngineType.JEXL;
    }

    @Override
    public ExpressionResult evaluate(String expression, ExpressionContext context) {
        ExecutionContext ec = context.toExecutionContext();
        long start = System.currentTimeMillis();
        try {
            Object result = delegate.evaluate(expression, ec);
            return ExpressionResult.success(result, ExpressionEngineType.JEXL, expression, System.currentTimeMillis() - start);
        } catch (ExpressionEvaluationException e) {
            return ExpressionResult.failure(e.getMessage(), ExpressionEngineType.JEXL, expression);
        }
    }

    @Override
    public <T> T evaluate(String expression, ExpressionContext context, Class<T> resultType) {
        ExecutionContext ec = context.toExecutionContext();
        try {
            return delegate.evaluate(expression, ec, resultType);
        } catch (ExpressionEvaluationException e) {
            throw new RuntimeException("JEXL evaluation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean evaluateBoolean(String expression, ExpressionContext context) {
        ExecutionContext ec = context.toExecutionContext();
        try {
            return delegate.evaluateBoolean(expression, ec);
        } catch (ExpressionEvaluationException e) {
            throw new RuntimeException("JEXL boolean evaluation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isValid(String expression) {
        return delegate.isValid(expression);
    }
}
