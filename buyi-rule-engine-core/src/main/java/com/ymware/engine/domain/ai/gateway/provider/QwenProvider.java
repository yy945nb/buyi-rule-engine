package com.ymware.engine.domain.ai.gateway.provider;

import com.ymware.engine.domain.ai.gateway.LlmProviderConfig;
import com.ymware.engine.domain.ai.gateway.LlmProviderType;

/**
 * Alibaba Qwen provider (qwen-turbo, qwen-plus, qwen-max, etc.).
 * Uses OpenAI-compatible API at dashscope.aliyuncs.com.
 */
public class QwenProvider extends AbstractLlmProvider {

    public QwenProvider(LlmProviderConfig config) {
        super(config);
    }

    @Override
    public LlmProviderType getProviderType() {
        return LlmProviderType.QWEN;
    }
}
