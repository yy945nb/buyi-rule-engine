package com.ymware.engine.expression.unified;

import com.ymware.engine.domain.rule.model.ExecutionContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Unified context wrapper for expression evaluation.
 * <p>
 * An {@code ExpressionContext} can be constructed either from an existing
 * {@link ExecutionContext} (the domain model used during rule execution) or from
 * a raw {@code Map<String, Object>} of variables. This abstraction allows every
 * {@link ExpressionEngineAdapter} to accept a single context type regardless of
 * the caller's original data format.
 * </p>
 */
public final class ExpressionContext {

    private final ExecutionContext executionContext;
    private final Map<String, Object> rawVariables;
    private final Map<String, Object> resources;

    /** Cached result of {@link #toExecutionContext()}. */
    private volatile ExecutionContext cachedExecutionContext;

    // ──────────────────────────────────────────────
    //  Private constructors
    // ──────────────────────────────────────────────

    private ExpressionContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
        this.rawVariables = null;
        this.resources = null;
    }

    private ExpressionContext(Map<String, Object> rawVariables, Map<String, Object> resources) {
        this.executionContext = null;
        this.rawVariables = rawVariables != null ? rawVariables : Collections.emptyMap();
        this.resources = resources != null ? resources : Collections.emptyMap();
    }

    // ──────────────────────────────────────────────
    //  Factory methods
    // ──────────────────────────────────────────────

    /**
     * Create an {@code ExpressionContext} that wraps an existing {@link ExecutionContext}.
     *
     * @param ctx the domain execution context; must not be {@code null}
     * @return a new {@code ExpressionContext}
     */
    public static ExpressionContext from(ExecutionContext ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("ExecutionContext must not be null");
        }
        return new ExpressionContext(ctx);
    }

    /**
     * Create an {@code ExpressionContext} backed by a raw variables map.
     *
     * @param variables the variables to use during expression evaluation; must not be {@code null}
     * @return a new {@code ExpressionContext}
     */
    public static ExpressionContext from(Map<String, Object> variables) {
        if (variables == null) {
            throw new IllegalArgumentException("Variables map must not be null");
        }
        return new ExpressionContext(variables, null);
    }

    /**
     * Create an {@code ExpressionContext} backed by separate variables and resources maps.
     *
     * @param variables the variables to use during expression evaluation
     * @param resources the resources available during expression evaluation (e.g. HTTP clients, DB connections)
     * @return a new {@code ExpressionContext}
     */
    public static ExpressionContext of(Map<String, Object> variables, Map<String, Object> resources) {
        return new ExpressionContext(variables, resources);
    }

    // ──────────────────────────────────────────────
    //  Accessors
    // ──────────────────────────────────────────────

    /**
     * Returns the variables map for expression evaluation.
     * <p>
     * If this context wraps an {@link ExecutionContext}, this returns its
     * {@link ExecutionContext#getAllVariables()}; otherwise it returns the raw map.
     * </p>
     *
     * @return an unmodifiable view of the variables
     */
    public Map<String, Object> getVariables() {
        if (executionContext != null) {
            return executionContext.getAllVariables();
        }
        return Collections.unmodifiableMap(rawVariables);
    }

    /**
     * Returns whether this context was created from an {@link ExecutionContext}.
     *
     * @return {@code true} if an {@link ExecutionContext} is present
     */
    public boolean hasExecutionContext() {
        return executionContext != null;
    }

    /**
     * Returns whether this context was created from a raw map (not an {@link ExecutionContext}).
     *
     * @return {@code true} if backed by a raw variables map
     */
    public boolean hasRawMap() {
        return executionContext == null;
    }

    /**
     * Converts this context to an {@link ExecutionContext}.
     * <p>
     * If this context already wraps an {@link ExecutionContext}, the same instance is
     * returned. Otherwise a new {@link ExecutionContext} is created from the raw variables
     * and resources, and the result is cached for subsequent calls.
     * </p>
     *
     * @return the {@link ExecutionContext}; never {@code null}
     */
    public ExecutionContext toExecutionContext() {
        if (executionContext != null) {
            return executionContext;
        }
        ExecutionContext cached = cachedExecutionContext;
        if (cached == null) {
            synchronized (this) {
                cached = cachedExecutionContext;
                if (cached == null) {
                    cached = new ExecutionContext();
                    cached.setVariables(rawVariables);
                    if (resources != null) {
                        resources.forEach(cached::registerResource);
                    }
                    cachedExecutionContext = cached;
                }
            }
        }
        return cachedExecutionContext;
    }

    /**
     * Converts this context to a plain {@code Map<String, Object>}.
     * <p>
     * The returned map contains the variables only (not resources or execution metadata).
     * </p>
     *
     * @return a mutable copy of the variables
     */
    public Map<String, Object> toMap() {
        return new HashMap<>(getVariables());
    }
}
