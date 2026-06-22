package com.ymware.engine.expression.unified;

/**
 * Adapter interface that bridges a specific expression engine to the unified
 * expression evaluation framework.
 * <p>
 * Each supported engine (JEXL, Aviator, SpEL, GraalJS, Java, Groovy) provides
 * its own implementation of this interface. The {@link DefaultUnifiedExpressionEngine}
 * maintains a registry of adapters and delegates evaluation calls to the correct
 * one based on the requested {@link ExpressionEngineType}.
 * </p>
 */
public interface ExpressionEngineAdapter {

    /**
     * Returns the engine type this adapter supports.
     *
     * @return the {@link ExpressionEngineType} handled by this adapter
     */
    ExpressionEngineType getEngineType();

    /**
     * Evaluate the given expression within the supplied context.
     *
     * @param expression the expression string to evaluate
     * @param context    the evaluation context providing variables and resources
     * @return an {@link ExpressionResult} containing the outcome of the evaluation
     */
    ExpressionResult evaluate(String expression, ExpressionContext context);

    /**
     * Evaluate the given expression and cast the result to the requested type.
     *
     * @param <T>        the expected result type
     * @param expression the expression string to evaluate
     * @param context    the evaluation context providing variables and resources
     * @param resultType the class of the expected result type
     * @return the evaluated value cast to {@code resultType}
     * @throws ClassCastException if the result cannot be cast to {@code resultType}
     */
    <T> T evaluate(String expression, ExpressionContext context, Class<T> resultType);

    /**
     * Evaluate the given expression as a boolean.
     * <p>
     * This is a convenience method; implementations may provide engine-specific
     * optimisations for boolean expressions.
     * </p>
     *
     * @param expression the expression string to evaluate
     * @param context    the evaluation context providing variables and resources
     * @return the boolean result of the evaluation
     */
    boolean evaluateBoolean(String expression, ExpressionContext context);

    /**
     * Check whether the given expression is syntactically valid for this engine
     * without actually evaluating it.
     *
     * @param expression the expression string to validate
     * @return {@code true} if the expression is syntactically valid
     */
    boolean isValid(String expression);
}
