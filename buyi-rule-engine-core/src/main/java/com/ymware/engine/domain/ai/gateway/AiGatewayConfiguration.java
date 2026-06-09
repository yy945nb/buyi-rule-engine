package com.ymware.engine.domain.ai.gateway;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring auto-configuration for the AI Gateway.
 * Registers the provider registry and gateway service.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(AiGatewayConfiguration.AiGatewayProperties.class)
public class AiGatewayConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LlmProviderRegistry llmProviderRegistry(AiGatewayProperties properties) {
        LlmProviderRegistry registry = new LlmProviderRegistry();

        // Register providers from config
        if (properties.getProviders() != null) {
            for (LlmProviderConfig config : properties.getProviders()) {
                try {
                    // Fill in defaults from provider type
                    if (config.getProviderType() == null) {
                        config.setProviderType(LlmProviderType.fromCode(config.getId()));
                    }
                    if (config.getApiHost() == null) {
                        config.setApiHost(config.getProviderType().getDefaultApiHost());
                    }
                    registry.register(config);
                } catch (Exception e) {
                    log.error("Failed to register LLM provider: {}", config.getId(), e);
                }
            }
        }

        // Register model aliases
        if (properties.getAliases() != null) {
            properties.getAliases().forEach(registry::registerAlias);
        }

        log.info("AI Gateway initialized with {} providers", registry.getAllProviders().size());
        return registry;
    }

    @Bean
    @ConditionalOnMissingBean
    public LlmGatewayService llmGatewayService(LlmProviderRegistry registry) {
        return new LlmGatewayService(registry);
    }

    /**
     * Configuration properties for AI Gateway.
     * Bind to: ai.gateway.* in application.yml
     */
    @Data
    @ConfigurationProperties(prefix = "ai.gateway")
    public static class AiGatewayProperties {

        /** Provider configurations */
        private List<LlmProviderConfig> providers = new ArrayList<>();

        /** Model aliases: alias -> providerId:modelName */
        private Map<String, String> aliases = new HashMap<>();
    }
}
