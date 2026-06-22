package com.ymware.gateway.core.protocol;

import com.ymware.gateway.sdk.model.ProtocolType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Anthropic Messages API 协议适配器
 * <p>
 * SSE 事件完整序列：message_start → content_block_start → content_block_delta(多个)
 * → content_block_stop → message_delta → message_stop。
 * 委托 SDK 的 {@link com.ymware.gateway.sdk.protocol.AnthropicProtocolAdapter}，
 * 通过 {@link AbstractSseProtocolAdapter} 完成类型转换和 SSE 桥接。
 * </p>
 */
@Component
public class AnthropicProtocolAdapter extends AbstractSseProtocolAdapter {

    private final com.ymware.gateway.sdk.protocol.AnthropicProtocolAdapter sdkAdapter;

    public AnthropicProtocolAdapter(ObjectMapper objectMapper,
                                     com.ymware.gateway.sdk.protocol.AnthropicProtocolAdapter sdkAdapter) {
        super(objectMapper);
        this.sdkAdapter = sdkAdapter;
    }

    @Override
    protected com.ymware.gateway.sdk.protocol.AnthropicProtocolAdapter sdkAdapter() {
        return sdkAdapter;
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.ANTHROPIC;
    }
}
