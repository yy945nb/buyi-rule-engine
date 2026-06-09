package com.ymware.engine.util;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import java.lang.reflect.Method;

/**
 * SpEL Utils
 */
public class SpelUtils {

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    public static String parse(Method method, Object[] args, String spel) {
        return resolve(method, args, spel, String.class);
    }

    public static <T> T resolve(Method method, Object[] args, String spel, Class<T> clazz) {
        String[] params = NAME_DISCOVERER.getParameterNames(method);
        if (params == null || params.length == 0) {
            return null;
        }
        EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);
        }
        Expression expression = PARSER.parseExpression(spel);
        return expression.getValue(context, clazz);
    }

}
