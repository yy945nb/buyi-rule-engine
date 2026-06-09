package com.ymware.engine.compute.function;

import com.ymware.engine.annotation.Executor;
import com.ymware.engine.annotation.Function;
import com.ymware.engine.annotation.Param;
import com.ymware.engine.expression.ExpressionEvaluator;
import com.ymware.engine.domain.rule.model.ExecutionContext;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * JEXL expression execution function.
 */
@Slf4j
@Function("jexlExpressionFunction")
public class JexlFunction {

    @Resource
    private ExpressionEvaluator expressionEvaluator;

    @Executor
    public Object executor(@Param(value = "expression") String expression,
                           @Param(value = "params", required = false) Map<String, Object> params) {
        if (log.isDebugEnabled()) {
            log.debug("Executing JEXL function with expression: {}, params: {}", expression, params);
        }
        ExecutionContext context = new ExecutionContext();
        if (params != null) {
            context.setVariables(params);
        }
        try {
            return expressionEvaluator.evaluate(expression, context);
        } catch (Exception e) {
            log.error("JEXL expression evaluation failed", e);
            throw new RuntimeException("JEXL evaluation failed: " + e.getMessage(), e);
        }
    }
}
