package com.ymware.engine.compute.config;

import com.ymware.engine.compute.api.DocumentApiExecutor;
import com.ymware.engine.compute.api.ExpressVariableTypeDocumentLoader;
import com.ymware.engine.compute.api.ExpressionAsyncThreadExecutor;
import com.ymware.engine.compute.api.thread.ExpressionSpringThreadExecutor;
import com.ymware.engine.compute.collect.ExecutorTraceCollectIntercept;
import com.ymware.engine.compute.collect.ExpressionDocCollect;
import com.ymware.engine.compute.config.props.ExpressionProperties;
import com.ymware.engine.compute.factory.ExpressionExecutorFactory;
import com.ymware.engine.compute.feature.ExpressionConfigIdFilterSupport;
import com.ymware.engine.compute.feature.ExpressionFunctionNameFilterSupport;
import com.ymware.engine.compute.feature.ExpressionVarConfigRegisterSupport;
import com.ymware.engine.compute.log.LogHelper;
import com.ymware.engine.compute.log.LogTraceService;
import com.ymware.engine.compute.log.Sl4jLogServiceImpl;
import com.ymware.engine.compute.process.ExpressDocumentManager;
import com.ymware.engine.compute.process.ExpressVariableTypeDocumentLoaderImpl;
import com.ymware.engine.compute.process.ExpressionVariableManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(value = {ExpressionProperties.class})
@Import({ExpClientEnginAutoConfiguration.class})
@ComponentScan(basePackages = {"com.ymware.engine.compute.function", "com.ymware.engine.compute.variable"})
@Slf4j
public class ExpressionConfiguration {

    /**
     * 开启链路追踪日志
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.plugin.express", value = "enable-trace-log", havingValue = "true", matchIfMissing = true)
    public ExecutorTraceCollectIntercept executorTraceCollectIntercept() {
        log.info("expression -> 【启用表达式日志追踪能力】");
        return new ExecutorTraceCollectIntercept();
    }

    /**
     * 配置异步线程池
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ExpressionAsyncThreadExecutor expressionSpringThreadExecutor() {
        return new ExpressionSpringThreadExecutor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExpressionDocCollect expressionDocCollect() {
        return new ExpressionDocCollect();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExpressVariableTypeDocumentLoader expressVariableTypeDocumentLoader() {
        return new ExpressVariableTypeDocumentLoaderImpl();
    }

    @Bean
    public ExpressionExecutorFactory expressionManager() {
        return new ExpressionExecutorFactory();
    }

    @Bean
    @ConditionalOnMissingBean(value = DocumentApiExecutor.class)
    public ExpressDocumentManager expressDocumentManager() {
        return new ExpressDocumentManager();
    }


    @Bean
    @ConditionalOnMissingBean(value = {LogTraceService.class})
    public LogTraceService sl4jLogService() {
        return new Sl4jLogServiceImpl();
    }

    @Bean
    public LogHelper logHelper() {
        return new LogHelper();
    }

    @Bean
    public ExpressionConfigIdFilterSupport expressionConfigIdFilter() {
        return new ExpressionConfigIdFilterSupport();
    }

    @Bean
    public ExpressionFunctionNameFilterSupport expressionFunctionNameFilterSupport() {
        return new ExpressionFunctionNameFilterSupport();
    }

    @Bean
    public ExpressionVarConfigRegisterSupport expressionVarConfigRegisterSupport() {
        return new ExpressionVarConfigRegisterSupport();
    }

    @Bean
    public ExpressionVariableManager expressionVariableManager() {
        return new ExpressionVariableManager();
    }
}
