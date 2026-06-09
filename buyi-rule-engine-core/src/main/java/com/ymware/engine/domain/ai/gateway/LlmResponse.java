package com.ymware.engine.domain.ai.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unified LLM response model across all providers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmResponse {

    /** Whether the request was successful */
    private boolean success;

    /** The generated text content */
    private String content;

    /** The model that actually served the request */
    private String model;

    /** Token usage statistics */
    private Usage usage;

    /** Error message if failed */
    private String errorMessage;

    /** Raw response from the provider (for debugging) */
    private String rawResponse;

    /** Provider type that served this request */
    private String providerType;

    /** Latency in milliseconds */
    private long latencyMs;

    public static LlmResponse success(String content, String model, String providerType) {
        return LlmResponse.builder()
                .success(true)
                .content(content)
                .model(model)
                .providerType(providerType)
                .build();
    }

    public static LlmResponse failure(String errorMessage, String providerType) {
        return LlmResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .providerType(providerType)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
    }
}
