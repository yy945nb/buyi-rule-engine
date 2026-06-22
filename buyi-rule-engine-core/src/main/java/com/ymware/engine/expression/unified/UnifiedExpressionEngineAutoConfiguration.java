package com.ymware.engine.expression.unified;

import com.ymware.engine.expression.ExpressionEvaluator;
import com.ymware.engine.service.ExpressionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 统一表达式引擎自动配置
 * 自动发现已有的 ExpressionEvaluator (JEXL) 和 ExpressionService (Aviator) bean，
 * 注册为适配器到统一引擎中
 */
@Configuration
public class UnifiedExpressionEngineAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(UnifiedExpressionEngine.class)
    public UnifiedExpressionEngine unifiedExpressionEngine(
            @Autowired(required = false) ExpressionEvaluator jexlEvaluator,
            @Autowired(required = false) ExpressionService aviatorService) {

        DefaultUnifiedExpressionEngine engine = new DefaultUnifiedExpressionEngine();

        if (jexlEvaluator != null) {
            engine.registerAdapter(new JexlEngineAdapter(jexlEvaluator));
        }

        if (aviatorService != null) {
            engine.registerAdapter(new AviatorEngineAdapter(aviatorService));
        }

        // 默认使用 JEXL，如果不可用则使用 AVIATOR
        if (jexlEvaluator != null) {
            engine.setDefaultEngineType(ExpressionEngineType.JEXL);
        } else if (aviatorService != null) {
            engine.setDefaultEngineType(ExpressionEngineType.AVIATOR);
        }

        return engine;
    }
}
