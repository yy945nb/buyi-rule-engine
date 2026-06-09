package com.ymware.engine.domain.ai.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Configuration for an LLM provider instance.
 * Can be stored in database and loaded dynamically.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmProviderConfig {

    /** Unique provider instance ID */
    private String id;

    /** Provider display name */
    private String name;

    /** Provider type */
    private LlmProviderType providerType;

    /** API base URL */
    private String apiHost;

    /** API key */
    private String apiKey;

    /** Default model to use */
    private String defaultModel;

    /** Available models for this provider */
    private java.util.List<String> availableModels;

    /** Default temperature */
    @Builder.Default
    private double defaultTemperature = 0.7;

    /** Default max tokens */
    private Integer defaultMaxTokens;

    /** Request timeout in ms */
    @Builder.Default
    private int timeoutMs = 60000;

    /** Max retries */
    @Builder.Default
    private int maxRetries = 2;

    /** Rate limit (requests per minute) */
    private Integer rateLimitRpm;

    /** Priority for provider selection (higher = preferred) */
    @Builder.Default
    private int priority = 0;

    /** Whether this provider is enabled */
    @Builder.Default
    private boolean enabled = true;

    /** Custom headers to include in requests */
    private Map<String, String> customHeaders;

    /** Provider-specific extra config */
    private Map<String, Object> extraConfig;
}
