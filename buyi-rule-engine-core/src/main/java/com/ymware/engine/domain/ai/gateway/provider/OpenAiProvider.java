package com.ymware.engine.domain.ai.gateway.provider;

import com.ymware.engine.domain.ai.gateway.LlmProviderConfig;
import com.ymware.engine.domain.ai.gateway.LlmProviderType;

/**
 * OpenAI provider (GPT-4, GPT-3.5-turbo, etc.).
 * Uses standard OpenAI chat completions API.
 */
public class OpenAiProvider extends AbstractLlmProvider {

    public OpenAiProvider(LlmProviderConfig config) {
        super(config);
    }

    @Override
    public LlmProviderType getProviderType() {
        return LlmProviderType.OPENAI;
    }
}
