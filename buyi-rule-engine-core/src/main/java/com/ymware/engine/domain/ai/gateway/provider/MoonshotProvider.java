package com.ymware.engine.domain.ai.gateway.provider;

import com.ymware.engine.domain.ai.gateway.LlmProviderConfig;
import com.ymware.engine.domain.ai.gateway.LlmProviderType;

/**
 * Moonshot AI (Kimi) provider.
 * Uses OpenAI-compatible API at api.moonshot.cn.
 */
public class MoonshotProvider extends AbstractLlmProvider {

    public MoonshotProvider(LlmProviderConfig config) {
        super(config);
    }

    @Override
    public LlmProviderType getProviderType() {
        return LlmProviderType.MOONSHOT;
    }
}
