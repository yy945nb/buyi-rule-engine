package com.ymware.engine.domain.ai.gateway.provider;

import com.ymware.engine.domain.ai.gateway.LlmProviderConfig;
import com.ymware.engine.domain.ai.gateway.LlmProviderType;

/**
 * Xiaomi Mimo provider.
 * Uses OpenAI-compatible API.
 */
public class MimoProvider extends AbstractLlmProvider {

    public MimoProvider(LlmProviderConfig config) {
        super(config);
    }

    @Override
    public LlmProviderType getProviderType() {
        return LlmProviderType.MIMO;
    }
}
