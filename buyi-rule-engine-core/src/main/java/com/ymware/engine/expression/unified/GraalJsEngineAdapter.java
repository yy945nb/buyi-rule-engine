package com.ymware.engine.expression.unified;

import javax.script.*;
import java.util.Map;

/**
 * GraalVM JavaScript 引擎适配器
 * 使用 JSR-223 ScriptEngine 接口
 */
public class GraalJsEngineAdapter implements ExpressionEngineAdapter {

    private final ScriptEngine engine;

    public GraalJsEngineAdapter() {
        this.engine = new ScriptEngineManager().getEngineByName("graal.js");
        if (this.engine == null) {
            throw new IllegalStateException("GraalJS engine not found. Ensure org.graalvm.js:js-scriptengine is on the classpath.");
        }
    }

    public GraalJsEngineAdapter(ScriptEngine engine) {
        this.engine = engine;
    }

    @Override
    public ExpressionEngineType getEngineType() {
        return ExpressionEngineType.GRAALJS;
    }

    @Override
    public ExpressionResult evaluate(String expression, ExpressionContext context) {
        long start = System.currentTimeMillis();
        try {
            Bindings bindings = engine.createBindings();
            bindings.put("polyglot.js.allowHostAccess", true);
            bindings.put("polyglot.js.allowHostClassLookup", true);
            Map<String, Object> variables = context.getVariables();
            if (variables != null) {
                bindings.putAll(variables);
            }
            Object result = engine.eval(wrapInFunction(expression), bindings);
            return ExpressionResult.success(result, ExpressionEngineType.GRAALJS, expression, System.currentTimeMillis() - start);
        } catch (Exception e) {
            return ExpressionResult.failure(e.getMessage(), ExpressionEngineType.GRAALJS, expression);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T evaluate(String expression, ExpressionContext context, Class<T> resultType) {
        ExpressionResult result = evaluate(expression, context);
        if (!result.isSuccess()) {
            throw new RuntimeException("GraalJS evaluation failed: " + result.getErrorMessage());
        }
        Object value = result.getValue();
        if (value == null) {
            return null;
        }
        if (resultType.isInstance(value)) {
            return (T) value;
        }
        throw new ClassCastException("GraalJS result type " + value.getClass().getName() + " cannot be cast to " + resultType.getName());
    }

    @Override
    public boolean evaluateBoolean(String expression, ExpressionContext context) {
        ExpressionResult result = evaluate(expression, context);
        if (!result.isSuccess()) {
            throw new RuntimeException("GraalJS boolean evaluation failed: " + result.getErrorMessage());
        }
        Object value = result.getValue();
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        return value != null;
    }

    @Override
    public boolean isValid(String expression) {
        try {
            if (engine instanceof Compilable) {
                ((Compilable) engine).compile(wrapInFunction(expression));
                return true;
            }
            // Without Compilable, assume valid (syntax errors will surface at eval time)
            return expression != null && !expression.trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private String wrapInFunction(String code) {
        return "function __eval__(){" + code + "};\n__eval__();";
    }
}
