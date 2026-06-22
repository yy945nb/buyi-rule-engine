package com.ymware.engine.expression.unified;

import com.ymware.engine.domain.rule.model.ExecutionContext;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UnifiedExpressionEngineTest {

    // ==================== ExpressionContext Tests ====================

    @Test
    void expressionContextFromExecutionContext() {
        ExecutionContext ec = new ExecutionContext();
        ec.setVariable("x", 10);
        ec.setVariable("y", "hello");

        ExpressionContext ctx = ExpressionContext.from(ec);

        assertTrue(ctx.hasExecutionContext());
        assertFalse(ctx.hasRawMap());
        assertEquals(10, ctx.getVariables().get("x"));
        assertEquals("hello", ctx.getVariables().get("y"));
    }

    @Test
    void expressionContextFromMap() {
        Map<String, Object> vars = Map.of("a", 1, "b", "test");

        ExpressionContext ctx = ExpressionContext.from(vars);

        assertFalse(ctx.hasExecutionContext());
        assertTrue(ctx.hasRawMap());
        assertEquals(1, ctx.getVariables().get("a"));
        assertEquals("test", ctx.getVariables().get("b"));
    }

    @Test
    void expressionContextToExecutionContextCaches() {
        Map<String, Object> vars = Map.of("x", 42);

        ExpressionContext ctx = ExpressionContext.from(vars);
        ExecutionContext ec1 = ctx.toExecutionContext();
        ExecutionContext ec2 = ctx.toExecutionContext();

        assertSame(ec1, ec2);
        assertEquals(42, ec1.getVariable("x"));
    }

    @Test
    void expressionContextToMapFromExecutionContext() {
        ExecutionContext ec = new ExecutionContext();
        ec.setVariable("key", "value");

        ExpressionContext ctx = ExpressionContext.from(ec);
        Map<String, Object> map = ctx.toMap();

        assertEquals("value", map.get("key"));
    }

    @Test
    void expressionContextOfWithResources() {
        Map<String, Object> vars = Map.of("v", 1);
        Map<String, Object> res = Map.of("db", "connection");

        ExpressionContext ctx = ExpressionContext.of(vars, res);

        assertTrue(ctx.hasRawMap());
        assertEquals(1, ctx.getVariables().get("v"));
    }

    // ==================== ExpressionResult Tests ====================

    @Test
    void expressionResultSuccess() {
        ExpressionResult result = ExpressionResult.success(42, ExpressionEngineType.JEXL, "1+1", 5);

        assertTrue(result.isSuccess());
        assertEquals(42, result.getValue());
        assertEquals(ExpressionEngineType.JEXL, result.getEngineType());
        assertEquals("1+1", result.getExpression());
        assertEquals(5, result.getExecutionTimeMs());
        assertNull(result.getErrorMessage());
    }

    @Test
    void expressionResultFailure() {
        ExpressionResult result = ExpressionResult.failure("syntax error", ExpressionEngineType.AVIATOR, "bad(");

        assertFalse(result.isSuccess());
        assertNull(result.getValue());
        assertEquals("syntax error", result.getErrorMessage());
        assertEquals(ExpressionEngineType.AVIATOR, result.getEngineType());
    }

    @Test
    void expressionResultGetValueTyped() {
        ExpressionResult result = ExpressionResult.success("hello", ExpressionEngineType.JEXL, "str", 1);

        assertEquals("hello", result.getValue(String.class));
        assertThrows(ClassCastException.class, () -> result.getValue(Integer.class));
    }

    @Test
    void expressionResultGetBooleanValue() {
        ExpressionResult trueResult = ExpressionResult.success(true, ExpressionEngineType.JEXL, "true", 1);
        ExpressionResult falseResult = ExpressionResult.success(false, ExpressionEngineType.JEXL, "false", 1);

        assertTrue(trueResult.getBooleanValue());
        assertFalse(falseResult.getBooleanValue());
    }

    @Test
    void expressionResultGetBooleanValueNullThrows() {
        ExpressionResult nullResult = ExpressionResult.success(null, ExpressionEngineType.JEXL, "null", 1);
        assertThrows(IllegalStateException.class, nullResult::getBooleanValue);
    }

    @Test
    void expressionResultGetValueNull() {
        ExpressionResult result = ExpressionResult.success(null, ExpressionEngineType.JEXL, "null", 1);

        assertNull(result.getValue());
        assertThrows(IllegalStateException.class, () -> result.getValue(String.class));
    }

    // ==================== DefaultUnifiedExpressionEngine Tests ====================

    @Test
    void defaultEngineRegistersAndRoutesToAdapter() {
        DefaultUnifiedExpressionEngine engine = new DefaultUnifiedExpressionEngine();

        ExpressionEngineAdapter mockAdapter = new ExpressionEngineAdapter() {
            @Override
            public ExpressionEngineType getEngineType() { return ExpressionEngineType.JEXL; }
            @Override
            public ExpressionResult evaluate(String expression, ExpressionContext context) {
                return ExpressionResult.success("mock_result", ExpressionEngineType.JEXL, expression, 0);
            }
            @Override
            public <T> T evaluate(String expression, ExpressionContext context, Class<T> resultType) { return null; }
            @Override
            public boolean evaluateBoolean(String expression, ExpressionContext context) { return true; }
            @Override
            public boolean isValid(String expression) { return true; }
        };

        engine.registerAdapter(mockAdapter);
        engine.setDefaultEngineType(ExpressionEngineType.JEXL);

        ExpressionContext ctx = ExpressionContext.from(Map.of());
        ExpressionResult result = engine.evaluate("test", ctx);

        assertTrue(result.isSuccess());
        assertEquals("mock_result", result.getValue());
    }

    @Test
    void defaultEngineThrowsForMissingAdapter() {
        DefaultUnifiedExpressionEngine engine = new DefaultUnifiedExpressionEngine();

        ExpressionContext ctx = ExpressionContext.from(Map.of());

        assertThrows(IllegalArgumentException.class,
                () -> engine.evaluate("test", ctx, ExpressionEngineType.GRAALJS));
    }

    @Test
    void defaultEngineSupportsEngine() {
        DefaultUnifiedExpressionEngine engine = new DefaultUnifiedExpressionEngine();

        assertFalse(engine.supportsEngine(ExpressionEngineType.JEXL));

        engine.registerAdapter(new ExpressionEngineAdapter() {
            @Override public ExpressionEngineType getEngineType() { return ExpressionEngineType.JEXL; }
            @Override public ExpressionResult evaluate(String e, ExpressionContext c) { return null; }
            @Override public <T> T evaluate(String e, ExpressionContext c, Class<T> t) { return null; }
            @Override public boolean evaluateBoolean(String e, ExpressionContext c) { return false; }
            @Override public boolean isValid(String e) { return true; }
        });

        assertTrue(engine.supportsEngine(ExpressionEngineType.JEXL));
        assertFalse(engine.supportsEngine(ExpressionEngineType.AVIATOR));
    }

    @Test
    void defaultEngineDefaultType() {
        DefaultUnifiedExpressionEngine engine = new DefaultUnifiedExpressionEngine();

        assertEquals(ExpressionEngineType.JEXL, engine.getDefaultEngineType());

        engine.setDefaultEngineType(ExpressionEngineType.AVIATOR);
        assertEquals(ExpressionEngineType.AVIATOR, engine.getDefaultEngineType());
    }

    @Test
    void defaultEngineEvaluateBoolean() {
        DefaultUnifiedExpressionEngine engine = new DefaultUnifiedExpressionEngine();

        engine.registerAdapter(new ExpressionEngineAdapter() {
            @Override public ExpressionEngineType getEngineType() { return ExpressionEngineType.JEXL; }
            @Override public ExpressionResult evaluate(String e, ExpressionContext c) { return null; }
            @Override public <T> T evaluate(String e, ExpressionContext c, Class<T> t) { return null; }
            @Override public boolean evaluateBoolean(String e, ExpressionContext c) { return e.equals("true"); }
            @Override public boolean isValid(String e) { return true; }
        });

        ExpressionContext ctx = ExpressionContext.from(Map.of());
        assertTrue(engine.evaluateBoolean("true", ctx, ExpressionEngineType.JEXL));
        assertFalse(engine.evaluateBoolean("false", ctx, ExpressionEngineType.JEXL));
    }

    @Test
    void defaultEngineIsValid() {
        DefaultUnifiedExpressionEngine engine = new DefaultUnifiedExpressionEngine();

        engine.registerAdapter(new ExpressionEngineAdapter() {
            @Override public ExpressionEngineType getEngineType() { return ExpressionEngineType.JEXL; }
            @Override public ExpressionResult evaluate(String e, ExpressionContext c) { return null; }
            @Override public <T> T evaluate(String e, ExpressionContext c, Class<T> t) { return null; }
            @Override public boolean evaluateBoolean(String e, ExpressionContext c) { return false; }
            @Override public boolean isValid(String e) { return !e.contains("!!!"); }
        });

        assertTrue(engine.isValid("valid", ExpressionEngineType.JEXL));
        assertFalse(engine.isValid("!!!invalid!!!", ExpressionEngineType.JEXL));
    }

    @Test
    void defaultEngineEvaluateWithMap() {
        DefaultUnifiedExpressionEngine engine = new DefaultUnifiedExpressionEngine();

        engine.registerAdapter(new ExpressionEngineAdapter() {
            @Override public ExpressionEngineType getEngineType() { return ExpressionEngineType.AVIATOR; }
            @Override
            public ExpressionResult evaluate(String expression, ExpressionContext context) {
                return ExpressionResult.success(context.getVariables().get("x"), ExpressionEngineType.AVIATOR, expression, 0);
            }
            @Override public <T> T evaluate(String e, ExpressionContext c, Class<T> t) { return null; }
            @Override public boolean evaluateBoolean(String e, ExpressionContext c) { return false; }
            @Override public boolean isValid(String e) { return true; }
        });

        ExpressionResult result = engine.evaluateWithMap("x", Map.of("x", 99), ExpressionEngineType.AVIATOR);
        assertTrue(result.isSuccess());
        assertEquals(99, result.getValue());
    }

    // ==================== ExpressionEngineType Tests ====================

    @Test
    void engineTypeValues() {
        ExpressionEngineType[] types = ExpressionEngineType.values();
        assertEquals(6, types.length);
        assertEquals(ExpressionEngineType.JEXL, ExpressionEngineType.valueOf("JEXL"));
        assertEquals(ExpressionEngineType.AVIATOR, ExpressionEngineType.valueOf("AVIATOR"));
        assertEquals(ExpressionEngineType.SPEL, ExpressionEngineType.valueOf("SPEL"));
        assertEquals(ExpressionEngineType.GRAALJS, ExpressionEngineType.valueOf("GRAALJS"));
        assertEquals(ExpressionEngineType.JAVA, ExpressionEngineType.valueOf("JAVA"));
        assertEquals(ExpressionEngineType.GROOVY, ExpressionEngineType.valueOf("GROOVY"));
    }
}
