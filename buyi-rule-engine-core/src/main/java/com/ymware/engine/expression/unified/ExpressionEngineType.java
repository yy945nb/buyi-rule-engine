package com.ymware.engine.expression.unified;

/**
 * Enumerates the supported expression engine types in the unified expression system.
 * Each value corresponds to a specific expression language runtime that can be
 * plugged into the rule engine via an {@link ExpressionEngineAdapter}.
 */
public enum ExpressionEngineType {

    /** Apache JEXL (Java Expression Language). */
    JEXL,

    /** Aviator - a high-performance expression evaluation engine. */
    AVIATOR,

    /** Spring Expression Language (SpEL). */
    SPEL,

    /** GraalJS - JavaScript engine powered by GraalVM. */
    GRAALJS,

    /** Native Java expression evaluation (compiled snippets). */
    JAVA,

    /** Apache Groovy scripting engine. */
    GROOVY
}
