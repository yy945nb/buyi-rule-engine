package com.ymware.engine.expression.unified;

import com.ymware.engine.domain.rule.model.ExecutionContext;

import java.util.Map;

/**
 * High-level unified expression engine interface.
 * <p>
 * This interface abstracts away the specific expression engine (JEXL, Aviator,
 * SpEL, GraalJS, Java, Groovy) and provides a single entry point for expression
 * evaluation throughout the rule engine. Internally it delegates to the
 * appropriate {@link ExpressionEngineAdapter} registered for the requested
 * {@link ExpressionEngineType}.
 * </p>
 */
public interface UnifiedExpressionEngine {

    // ──────────────────────────────────────────────
    //  Core evaluation
    // ──────────────────────────────────────────────

    /**
     * Evaluate an expression using the specified engine type.
     *
     * @param expression the expression string to evaluate
     * @param context    the evaluation context
     * @param engineType the engine to use for evaluation
     * @return the evaluation result
     */
    ExpressionResult evaluate(String expression, ExpressionContext context, ExpressionEngineType engineType);

    /**
     * Evaluate an expression using the default engine type.
     *
     * @param expression the expression string to evaluate
     * @param context    the evaluation context
     * @return the evaluation result
     */
    ExpressionResult evaluate(String expression, ExpressionContext context);

    /**
     * Evaluate an expression and cast the result to the requested type using
     * the specified engine type.
     *
     * @param <T>        the expected result type
     * @param expression the expression string to evaluate
     * @param context    the evaluation context
     * @param engineType the engine to use for evaluation
     * @param resultType the class of the expected result type
     * @return the evaluated value cast to {@code resultType}
     * @throws ClassCastException if the result cannot be cast to {@code resultType}
     */
    <T> T evaluate(String expression, ExpressionContext context, ExpressionEngineType engineType, Class<T> resultType);

    /**
     * Evaluate an expression as a boolean using the specified engine type.
     *
     * @param expression the expression string to evaluate
     * @param context    the evaluation context
     * @param engineType the engine to use for evaluation
     * @return the boolean result
     */
    boolean evaluateBoolean(String expression, ExpressionContext context, ExpressionEngineType engineType);

    // ──────────────────────────────────────────────
    //  Convenience methods
    // ──────────────────────────────────────────────

    /**
     * Evaluate an expression with an {@link ExecutionContext} directly.
     * <p>
     * This is a convenience method that wraps the {@link ExecutionContext} in an
     * {@link ExpressionContext} before delegating.
     * </p>
     *
     * @param expression the expression string to evaluate
     * @param context    the domain execution context
     * @param engineType the engine to use for evaluation
     * @return the evaluation result
     */
    ExpressionResult evaluateWithExecutionContext(String expression,
                                                  ExecutionContext context,
                                                  ExpressionEngineType engineType);

    /**
     * Evaluate an expression with a raw variables map directly.
     * <p>
     * This is a convenience method that wraps the variables map in an
     * {@link ExpressionContext} before delegating.
     * </p>
     *
     * @param expression the expression string to evaluate
     * @param variables  the variables map
     * @param engineType the engine to use for evaluation
     * @return the evaluation result
     */
    ExpressionResult evaluateWithMap(String expression,
                                     Map<String, Object> variables,
                                     ExpressionEngineType engineType);

    // ──────────────────────────────────────────────
    //  Validation
    // ──────────────────────────────────────────────

    /**
     * Check whether the given expression is syntactically valid for the
     * specified engine without evaluating it.
     *
     * @param expression the expression string to validate
     * @param engineType the engine whose syntax rules to apply
     * @return {@code true} if the expression is syntactically valid
     */
    boolean isValid(String expression, ExpressionEngineType engineType);

    // ──────────────────────────────────────────────
    //  Configuration
    // ──────────────────────────────────────────────

    /**
     * Returns the default engine type used when no engine type is explicitly
     * specified.
     *
     * @return the current default {@link ExpressionEngineType}
     */
    ExpressionEngineType getDefaultEngineType();

    /**
     * Sets the default engine type.
     *
     * @param type the new default {@link ExpressionEngineType}
     */
    void setDefaultEngineType(ExpressionEngineType type);

    /**
     * Checks whether an adapter is registered for the given engine type.
     *
     * @param type the engine type to check
     * @return {@code true} if the engine is supported
     */
    boolean supportsEngine(ExpressionEngineType type);
}
