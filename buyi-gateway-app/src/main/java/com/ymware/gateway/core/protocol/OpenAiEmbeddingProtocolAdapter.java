package com.ymware.gateway.core.protocol;

import com.ymware.gateway.sdk.model.ProtocolType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * OpenAI Embeddings 协议适配器（App 层）
 * <p>
 * 委托 SDK 的 {@link com.ymware.gateway.sdk.protocol.OpenAiEmbeddingProtocolAdapter}。
 * </p>
 */
@Component
public class OpenAiEmbeddingProtocolAdapter extends AbstractSdkProtocolAdapter {

    private final com.ymware.gateway.sdk.protocol.OpenAiEmbeddingProtocolAdapter sdkAdapter;

    public OpenAiEmbeddingProtocolAdapter(ObjectMapper objectMapper,
                                           com.ymware.gateway.sdk.protocol.OpenAiEmbeddingProtocolAdapter sdkAdapter) {
        super(objectMapper);
        this.sdkAdapter = sdkAdapter;
    }

    @Override
    protected com.ymware.gateway.sdk.protocol.OpenAiEmbeddingProtocolAdapter sdkAdapter() {
        return sdkAdapter;
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.OPENAI_EMBEDDING;
    }
}
