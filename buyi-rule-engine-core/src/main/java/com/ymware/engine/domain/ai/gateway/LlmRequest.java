package com.ymware.engine.domain.ai.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Unified LLM request model across all providers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmRequest {

    /** Provider type code (openai, zhipu, deepseek, qwen, ollama, custom) */
    private String providerType;

    /** Model name (e.g., gpt-4, glm-4, deepseek-chat, qwen-turbo) */
    private String model;

    /** Conversation messages */
    private List<LlmMessage> messages;

    /** Sampling temperature (0.0 - 2.0) */
    @Builder.Default
    private double temperature = 0.7;

    /** Max output tokens */
    private Integer maxTokens;

    /** System prompt (convenience field, merged into messages) */
    private String systemPrompt;

    /** User prompt (convenience field, merged into messages) */
    private String prompt;

    /** Whether to stream the response */
    @Builder.Default
    private boolean stream = false;

    /** Timeout in milliseconds */
    @Builder.Default
    private int timeoutMs = 60000;

    /** Retry count on failure */
    @Builder.Default
    private int retryCount = 2;

    /** Custom headers for the request */
    private java.util.Map<String, String> customHeaders;
}
