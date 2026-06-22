package com.ymware.engine.expression.unified;

import com.ymware.engine.workflow.tools.SpringExpressionParser;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import java.util.Map;

/**
 * SpEL 引擎适配器 - 包装 Spring Expression Language
 * 默认使用只读的 SimpleEvaluationContext 保证安全性
 */
public class SpelEngineAdapter implements ExpressionEngineAdapter {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public ExpressionEngineType getEngineType() {
        return ExpressionEngineType.SPEL;
    }

    @Override
    public ExpressionResult evaluate(String expression, ExpressionContext context) {
        long start = System.currentTimeMillis();
        try {
            SimpleEvaluationContext evalContext = SimpleEvaluationContext.forReadOnlyDataBinding().build();
            Map<String, Object> variables = context.getVariables();
            if (variables != null) {
                variables.forEach(evalContext::setVariable);
            }
            Object result = parser.parseExpression(expression).getValue(evalContext);
            return ExpressionResult.success(result, ExpressionEngineType.SPEL, expression, System.currentTimeMillis() - start);
        } catch (Exception e) {
            return ExpressionResult.failure(e.getMessage(), ExpressionEngineType.SPEL, expression);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T evaluate(String expression, ExpressionContext context, Class<T> resultType) {
        SimpleEvaluationContext evalContext = SimpleEvaluationContext.forReadOnlyDataBinding().build();
        Map<String, Object> variables = context.getVariables();
        if (variables != null) {
            variables.forEach(evalContext::setVariable);
        }
        return parser.parseExpression(expression).getValue(evalContext, resultType);
    }

    @Override
    public boolean evaluateBoolean(String expression, ExpressionContext context) {
        SimpleEvaluationContext evalContext = SimpleEvaluationContext.forReadOnlyDataBinding().build();
        Map<String, Object> variables = context.getVariables();
        if (variables != null) {
            variables.forEach(evalContext::setVariable);
        }
        Boolean result = parser.parseExpression(expression).getValue(evalContext, Boolean.class);
        return result != null && result;
    }

    @Override
    public boolean isValid(String expression) {
        try {
            parser.parseExpression(expression);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
