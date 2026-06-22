package com.ymware.engine.expression.unified;

import com.ymware.engine.service.ExpressionService;

import java.util.Map;

/**
 * Aviator 引擎适配器 - 包装已有的 ExpressionService 实现
 */
public class AviatorEngineAdapter implements ExpressionEngineAdapter {

    private final ExpressionService delegate;

    public AviatorEngineAdapter(ExpressionService delegate) {
        this.delegate = delegate;
    }

    @Override
    public ExpressionEngineType getEngineType() {
        return ExpressionEngineType.AVIATOR;
    }

    @Override
    public ExpressionResult evaluate(String expression, ExpressionContext context) {
        Map<String, Object> env = context.toMap();
        long start = System.currentTimeMillis();
        try {
            Object result = delegate.execute(expression, env);
            return ExpressionResult.success(result, ExpressionEngineType.AVIATOR, expression, System.currentTimeMillis() - start);
        } catch (Exception e) {
            return ExpressionResult.failure(e.getMessage(), ExpressionEngineType.AVIATOR, expression);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T evaluate(String expression, ExpressionContext context, Class<T> resultType) {
        Map<String, Object> env = context.toMap();
        Object result = delegate.execute(expression, env);
        if (result == null) {
            return null;
        }
        if (resultType.isInstance(result)) {
            return (T) result;
        }
        throw new ClassCastException("Aviator result type " + result.getClass().getName() + " cannot be cast to " + resultType.getName());
    }

    @Override
    public boolean evaluateBoolean(String expression, ExpressionContext context) {
        Map<String, Object> env = context.toMap();
        Object result = delegate.execute(expression, env);
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        if (result instanceof Number) {
            return ((Number) result).doubleValue() != 0;
        }
        if (result instanceof String) {
            return !((String) result).isEmpty();
        }
        return result != null;
    }

    @Override
    public boolean isValid(String expression) {
        try {
            delegate.validator(expression);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
