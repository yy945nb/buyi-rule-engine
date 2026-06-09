package com.ymware.engine.domain.ai.gateway.provider;

import com.ymware.engine.domain.ai.gateway.LlmProviderConfig;
import com.ymware.engine.domain.ai.gateway.LlmProviderType;

/**
 * DeepSeek provider (deepseek-chat, deepseek-coder, etc.).
 * Uses OpenAI-compatible API at api.deepseek.com.
 */
public class DeepSeekProvider extends AbstractLlmProvider {

    public DeepSeekProvider(LlmProviderConfig config) {
        super(config);
    }

    @Override
    public LlmProviderType getProviderType() {
        return LlmProviderType.DEEPSEEK;
    }
}
