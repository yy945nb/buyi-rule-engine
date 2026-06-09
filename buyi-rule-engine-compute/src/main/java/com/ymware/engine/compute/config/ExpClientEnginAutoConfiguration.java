package com.ymware.engine.compute.config;

import com.ymware.engine.compute.api.ClientEngineInvokeService;
import com.ymware.engine.compute.api.RemoteExpressionConfigService;
import com.ymware.engine.compute.api.config.ExpressionConfigCallManager;
import com.ymware.engine.compute.api.config.HttpCacheExpressionConfigService;
import com.ymware.engine.compute.api.config.HttpExpressionConfigService;
import com.ymware.engine.compute.api.config.RedisExpressionConfigService;
import com.ymware.engine.compute.api.configurability.CacheExpressionConfigurabilityProcessor;
import com.ymware.engine.compute.api.configurability.RedissonLockExpressionConfigurabilityProcessor;
import com.ymware.engine.compute.api.configurability.TraceSwitchConfigurabilityProcessor;
import com.ymware.engine.compute.engine.ClientEngineFactory;
import com.ymware.engine.compute.engine.LocalEngineServiceImpl;
import com.ymware.engine.compute.http.RestRemoteHttpService;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * 客户端引擎执行层
 */
@Configuration
public class ExpClientEnginAutoConfiguration {

    @Bean
    public ClientEngineFactory engineFactory() {
        return new ClientEngineFactory();
    }

    @Bean
    public ClientEngineInvokeService localEngineServiceImpl() {
        return new LocalEngineServiceImpl();
    }

    @Bean
    public RestRemoteHttpService remoteHttpService() {
        return new RestRemoteHttpService();
    }

    @Bean
    @LoadBalanced
    public RestTemplate loadRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public RemoteExpressionConfigService httpExpressionConfigService() {
        return new HttpExpressionConfigService();
    }

    @Bean
    public HttpCacheExpressionConfigService httpLocalCacheExpressionConfigService() {
        return new HttpCacheExpressionConfigService();
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public RedisExpressionConfigService redisExpressionConfigService() {
        return new RedisExpressionConfigService();
    }

    @Bean
    public ExpressionConfigCallManager expressionConfigCallManager() {
        return new ExpressionConfigCallManager();
    }

    @Bean
    public TraceSwitchConfigurabilityProcessor tracingProcessor() {
        return new TraceSwitchConfigurabilityProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheExpressionConfigurabilityProcessor cacheExpressionConfigurabilityProcessor() {
        return new CacheExpressionConfigurabilityProcessor();
    }

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    public RedissonLockExpressionConfigurabilityProcessor redissonLockExpressionConfig(RedissonClient redissonClient) {
        return new RedissonLockExpressionConfigurabilityProcessor(redissonClient);
    }

}
