package com.ymware.gateway.core.protocol;

import com.ymware.gateway.sdk.model.ProtocolType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * OpenAI Responses API 协议适配器
 * <p>
 * SSE 事件使用点分命名：response.output_text.delta、response.function_call_arguments.delta、response.completed
 * 错误格式复用 OpenAI 标准格式。
 * 委托 SDK 的 {@link com.ymware.gateway.sdk.protocol.OpenAiResponsesProtocolAdapter}，
 * 通过 {@link AbstractSseProtocolAdapter} 完成类型转换和 SSE 桥接。
 * </p>
 */
@Component
public class OpenAiResponsesProtocolAdapter extends AbstractSseProtocolAdapter {

    private final com.ymware.gateway.sdk.protocol.OpenAiResponsesProtocolAdapter sdkAdapter;

    public OpenAiResponsesProtocolAdapter(ObjectMapper objectMapper,
                                           com.ymware.gateway.sdk.protocol.OpenAiResponsesProtocolAdapter sdkAdapter) {
        super(objectMapper);
        this.sdkAdapter = sdkAdapter;
    }

    @Override
    protected com.ymware.gateway.sdk.protocol.OpenAiResponsesProtocolAdapter sdkAdapter() {
        return sdkAdapter;
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.OPENAI_RESPONSES;
    }
}
