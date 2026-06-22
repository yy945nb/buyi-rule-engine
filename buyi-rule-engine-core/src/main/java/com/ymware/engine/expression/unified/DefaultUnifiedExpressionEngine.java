package com.ymware.engine.expression.unified;

import com.ymware.engine.domain.rule.model.ExecutionContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link UnifiedExpressionEngine}.
 * <p>
 * Maintains a registry of {@link ExpressionEngineAdapter} instances keyed by
 * {@link ExpressionEngineType}. When an evaluation request arrives, the engine
 * looks up the appropriate adapter and delegates the work to it. New adapters
 * can be registered at runtime via {@link #registerAdapter(ExpressionEngineAdapter)}.
 * </p>
 */
public class DefaultUnifiedExpressionEngine implements UnifiedExpressionEngine {

    private final ConcurrentHashMap<ExpressionEngineType, ExpressionEngineAdapter> adapters =
            new ConcurrentHashMap<>();

    private volatile ExpressionEngineType defaultEngineType = ExpressionEngineType.JEXL;

    // ──────────────────────────────────────────────
    //  Adapter registration
    // ──────────────────────────────────────────────

    /**
     * Register an expression engine adapter.
     * <p>
     * If an adapter for the same engine type is already registered, it will be
     * replaced.
     * </p>
     *
     * @param adapter the adapter to register; must not be {@code null}
     */
    public void registerAdapter(ExpressionEngineAdapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("Adapter must not be null");
        }
        adapters.put(adapter.getEngineType(), adapter);
    }

    // ──────────────────────────────────────────────
    //  Core evaluation
    // ──────────────────────────────────────────────

    @Override
    public ExpressionResult evaluate(String expression, ExpressionContext context, ExpressionEngineType engineType) {
        ExpressionEngineAdapter adapter = resolveAdapter(engineType);
        return adapter.evaluate(expression, context);
    }

    @Override
    public ExpressionResult evaluate(String expression, ExpressionContext context) {
        return evaluate(expression, context, defaultEngineType);
    }

    @Override
    public <T> T evaluate(String expression, ExpressionContext context, ExpressionEngineType engineType, Class<T> resultType) {
        ExpressionEngineAdapter adapter = resolveAdapter(engineType);
        return adapter.evaluate(expression, context, resultType);
    }

    @Override
    public boolean evaluateBoolean(String expression, ExpressionContext context, ExpressionEngineType engineType) {
        ExpressionEngineAdapter adapter = resolveAdapter(engineType);
        return adapter.evaluateBoolean(expression, context);
    }

    // ──────────────────────────────────────────────
    //  Convenience methods
    // ──────────────────────────────────────────────

    @Override
    public ExpressionResult evaluateWithExecutionContext(String expression,
                                                         ExecutionContext context,
                                                         ExpressionEngineType engineType) {
        ExpressionContext exprCtx = ExpressionContext.from(context);
        return evaluate(expression, exprCtx, engineType);
    }

    @Override
    public ExpressionResult evaluateWithMap(String expression,
                                            Map<String, Object> variables,
                                            ExpressionEngineType engineType) {
        ExpressionContext exprCtx = ExpressionContext.from(variables);
        return evaluate(expression, exprCtx, engineType);
    }

    // ──────────────────────────────────────────────
    //  Validation
    // ──────────────────────────────────────────────

    @Override
    public boolean isValid(String expression, ExpressionEngineType engineType) {
        ExpressionEngineAdapter adapter = resolveAdapter(engineType);
        return adapter.isValid(expression);
    }

    // ──────────────────────────────────────────────
    //  Configuration
    // ──────────────────────────────────────────────

    @Override
    public ExpressionEngineType getDefaultEngineType() {
        return defaultEngineType;
    }

    @Override
    public void setDefaultEngineType(ExpressionEngineType type) {
        if (type == null) {
            throw new IllegalArgumentException("Default engine type must not be null");
        }
        this.defaultEngineType = type;
    }

    @Override
    public boolean supportsEngine(ExpressionEngineType type) {
        return adapters.containsKey(type);
    }

    // ──────────────────────────────────────────────
    //  Internal helpers
    // ──────────────────────────────────────────────

    /**
     * Resolve the adapter for the given engine type.
     *
     * @param engineType the engine type to look up
     * @return the registered adapter
     * @throws IllegalArgumentException if no adapter is registered for the given type
     */
    private ExpressionEngineAdapter resolveAdapter(ExpressionEngineType engineType) {
        ExpressionEngineAdapter adapter = adapters.get(engineType);
        if (adapter == null) {
            throw new IllegalArgumentException(
                    "No ExpressionEngineAdapter registered for engine type: " + engineType);
        }
        return adapter;
    }
}
