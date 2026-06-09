package com.ymware.engine.domain.ai.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Routing result from the model router.
 * Inspired by AI-Gateway's RouteResult.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmRouteResult {

    /** The provider type to use */
    private LlmProviderType providerType;

    /** The provider instance ID */
    private String providerId;

    /** The actual model name to send to the provider */
    private String targetModel;

    /** Provider display name for logging */
    private String providerName;

    /** Priority (higher = preferred) */
    @Builder.Default
    private int priority = 0;

    /**
     * Get the provider code for logging.
     */
    public String getProviderCode() {
        return providerType.getCode() + ":" + providerId;
    }
}
