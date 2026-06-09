package com.ymware.engine.domain.ai.gateway;

/**
 * Unified interface for LLM providers.
 * Inspired by AI-Gateway's ProviderClient pattern.
 *
 * Each provider implementation handles provider-specific HTTP calls,
 * auth headers, and response parsing while presenting a consistent interface.
 */
public interface LlmProvider {

    /**
     * Get the provider type.
     */
    LlmProviderType getProviderType();

    /**
     * Get the unique provider instance ID.
     */
    String getProviderId();

    /**
     * Send a chat completion request (non-streaming).
     *
     * @param request the unified request
     * @return the unified response
     */
    LlmResponse chat(LlmRequest request);

    /**
     * Check if this provider is available and healthy.
     */
    boolean isAvailable();

    /**
     * Get the provider configuration.
     */
    LlmProviderConfig getConfig();

    /**
     * Get display name for logging.
     */
    default String getDisplayName() {
        return getConfig() != null ? getConfig().getName() : getProviderType().getDisplayName();
    }

    /**
     * Get the provider type code for routing.
     */
    default String getProviderCode() {
        return getProviderType().getCode() + ":" + getProviderId();
    }
}
