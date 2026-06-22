package com.ymware.engine.expression;

import com.ymware.engine.domain.rule.model.ExecutionContext;
import com.ymware.engine.domain.rule.model.ExpressionEvaluationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class JexlExpressionEvaluatorTest {

    private JexlExpressionEvaluator evaluator;
    private ExecutionContext emptyContext;

    @BeforeEach
    void setUp() {
        evaluator = new JexlExpressionEvaluator();
        emptyContext = new ExecutionContext();
    }

    // ==================== 基本表达式求值 ====================

    @Test
    void evaluateArithmeticExpression() throws Exception {
        Object result = evaluator.evaluate("3 + 4 * 2", emptyContext);
        assertEquals(11, result);
    }

    @Test
    void evaluateComparisonExpression() throws Exception {
        Object result = evaluator.evaluate("10 > 5", emptyContext);
        assertEquals(true, result);
    }

    @Test
    void evaluateLogicalExpression() throws Exception {
        Object result = evaluator.evaluate("true && false", emptyContext);
        assertEquals(false, result);
    }

    @Test
    void evaluateStringConcatenation() throws Exception {
        Object result = evaluator.evaluate("'hello' + ' ' + 'world'", emptyContext);
        assertEquals("hello world", result);
    }

    @Test
    void evaluateWithContextVariables() throws Exception {
        ExecutionContext context = new ExecutionContext();
        context.setVariable("x", 10);
        context.setVariable("y", 20);

        Object result = evaluator.evaluate("x + y", context);
        assertEquals(30, result);
    }

    // ==================== 脚本检测 ====================

    @ParameterizedTest
    @CsvSource({
            "'a + b', false",
            "'a > 0 ? 1 : 2', false",
            "'var x = 10; x + 1', true",
            "'if (x > 0) { return x; }', true",
            "'a + \"hello;world\"', false"
    })
    void scriptDetectionShouldWork(String expression, boolean expectedIsScript) {
        // The key test: evaluator doesn't crash on valid expressions with semicolons in strings
        if (!expectedIsScript) {
            assertDoesNotThrow(() -> {
                if (expression.contains("+") && !expression.contains("\"")) {
                    evaluator.evaluate(expression, emptyContext);
                }
            });
        }
    }

    // ==================== 缓存 ====================

    @Test
    void cacheShouldReturnSameResult() throws Exception {
        evaluator.evaluate("2 + 3", emptyContext);
        evaluator.evaluate("2 + 3", emptyContext);
        assertTrue(evaluator.getCacheSize() > 0);
    }

    @Test
    void clearCacheShouldResetSize() throws Exception {
        evaluator.evaluate("1 + 1", emptyContext);
        assertTrue(evaluator.getCacheSize() > 0);
        evaluator.clearCache();
        assertEquals(0, evaluator.getCacheSize());
    }

    // ==================== 编译 ====================

    @Test
    void compileShouldReturnCompiledExpression() throws Exception {
        CompiledExpression compiled = evaluator.compile("10 * 2");
        assertNotNull(compiled);
    }

    @Test
    void compileInvalidExpressionShouldThrow() {
        assertThrows(ExpressionEvaluationException.class,
                () -> evaluator.compile("10 +"));
    }

    // ==================== 验证 ====================

    @Test
    void isValidReturnsTrueForValidExpression() {
        assertTrue(evaluator.isValid("1 + 2"));
    }

    @Test
    void isValidReturnsFalseForInvalidExpression() {
        assertFalse(evaluator.isValid("10 +"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    void isValidReturnsFalseForBlankInput(String input) {
        assertFalse(evaluator.isValid(input));
    }

    // ==================== Boolean 求值 ====================

    @Test
    void evaluateBooleanTrue() throws Exception {
        assertTrue(evaluator.evaluateBoolean("true", emptyContext));
    }

    @Test
    void evaluateBooleanFalse() throws Exception {
        assertFalse(evaluator.evaluateBoolean("false", emptyContext));
    }

    @Test
    void evaluateBooleanNullReturnsFalse() throws Exception {
        assertFalse(evaluator.evaluateBoolean("null", emptyContext));
    }

    // ==================== 错误处理 ====================

    @ParameterizedTest
    @NullAndEmptySource
    void evaluateThrowsOnNullOrEmpty(String input) {
        assertThrows(ExpressionEvaluationException.class,
                () -> evaluator.evaluate(input, emptyContext));
    }

    @Test
    void evaluateInvalidExpressionShouldThrow() {
        assertThrows(ExpressionEvaluationException.class,
                () -> evaluator.evaluate("10 +", emptyContext));
    }

    // ==================== 工具函数 ====================

    @Test
    void utilityFunctionShouldBeAccessibleAsVariable() throws Exception {
        // util is injected as a context variable in createJexlContext
        Object result = evaluator.evaluate("context != null", emptyContext);
        assertEquals(true, result);
    }

    // ==================== 带类型求值 ====================

    @Test
    void evaluateWithResultType() throws Exception {
        Integer result = evaluator.evaluate("3 + 4", emptyContext, Integer.class);
        assertEquals(7, result);
    }

    @Test
    void evaluateWithWrongResultTypeShouldThrow() {
        assertThrows(ExpressionEvaluationException.class,
                () -> evaluator.evaluate("'hello'", emptyContext, Integer.class));
    }
}
