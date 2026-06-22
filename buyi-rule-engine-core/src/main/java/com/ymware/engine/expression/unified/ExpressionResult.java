package com.ymware.engine.expression.unified;

import lombok.Getter;

/**
 * Immutable result of an expression evaluation.
 * <p>
 * Every evaluation performed through {@link ExpressionEngineAdapter} or
 * {@link UnifiedExpressionEngine} returns an {@code ExpressionResult}, whether
 * the evaluation succeeded or failed. This uniform return type lets callers
 * inspect timing, engine type, and error details without catching exceptions.
 * </p>
 */
@Getter
public final class ExpressionResult {

    /** The value produced by the expression, or {@code null} if the evaluation failed. */
    private final Object value;

    /** The engine that performed the evaluation. */
    private final ExpressionEngineType engineType;

    /** The expression string that was evaluated. */
    private final String expression;

    /** Time taken to evaluate the expression, in milliseconds. */
    private final long executionTimeMs;

    /** Whether the evaluation completed without error. */
    private final boolean success;

    /** Human-readable error message, or {@code null} if the evaluation succeeded. */
    private final String errorMessage;

    // ──────────────────────────────────────────────
    //  Private constructor
    // ──────────────────────────────────────────────

    private ExpressionResult(Object value,
                             ExpressionEngineType engineType,
                             String expression,
                             long executionTimeMs,
                             boolean success,
                             String errorMessage) {
        this.value = value;
        this.engineType = engineType;
        this.expression = expression;
        this.executionTimeMs = executionTimeMs;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    // ──────────────────────────────────────────────
    //  Static factories
    // ──────────────────────────────────────────────

    /**
     * Create a successful evaluation result.
     *
     * @param value           the computed value
     * @param engineType      the engine that produced the result
     * @param expression      the expression that was evaluated
     * @param executionTimeMs evaluation duration in milliseconds
     * @return a new {@code ExpressionResult} representing success
     */
    public static ExpressionResult success(Object value,
                                           ExpressionEngineType engineType,
                                           String expression,
                                           long executionTimeMs) {
        return new ExpressionResult(value, engineType, expression, executionTimeMs, true, null);
    }

    /**
     * Create a failed evaluation result.
     *
     * @param errorMessage    description of the failure
     * @param engineType      the engine that attempted the evaluation
     * @param expression      the expression that was evaluated
     * @return a new {@code ExpressionResult} representing failure
     */
    public static ExpressionResult failure(String errorMessage,
                                           ExpressionEngineType engineType,
                                           String expression) {
        return new ExpressionResult(null, engineType, expression, 0L, false, errorMessage);
    }

    // ──────────────────────────────────────────────
    //  Typed accessors
    // ──────────────────────────────────────────────

    /**
     * Returns the value cast to the specified type.
     *
     * @param <T>  the expected value type
     * @param type the class of the expected value type
     * @return the typed value
     * @throws IllegalStateException if the value is {@code null}
     * @throws ClassCastException    if the value cannot be cast to {@code type}
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(Class<T> type) {
        if (value == null) {
            throw new IllegalStateException("Expression result value is null");
        }
        if (!type.isInstance(value)) {
            throw new ClassCastException("Expression result value is of type "
                    + value.getClass().getName() + ", not " + type.getName());
        }
        return (T) value;
    }

    /**
     * Returns the value as a {@link Boolean}.
     * <p>
     * Handles the following conversions:
     * <ul>
     *     <li>{@link Boolean} - returned directly</li>
     *     <li>{@link Number} - {@code true} if the value is non-zero</li>
     *     <li>{@link String} - parsed via {@link Boolean#parseBoolean(String)}</li>
     * </ul>
     * </p>
     *
     * @return the boolean value
     * @throws IllegalStateException if the value is {@code null}
     * @throws ClassCastException    if the value cannot be converted to a boolean
     */
    public boolean getBooleanValue() {
        if (value == null) {
            throw new IllegalStateException("Expression result value is null");
        }
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof Number n) {
            return n.doubleValue() != 0.0;
        }
        if (value instanceof String s) {
            return Boolean.parseBoolean(s);
        }
        throw new ClassCastException("Cannot convert value of type "
                + value.getClass().getName() + " to boolean");
    }
}
