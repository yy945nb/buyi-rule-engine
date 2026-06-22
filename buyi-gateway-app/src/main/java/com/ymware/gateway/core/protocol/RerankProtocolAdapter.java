package com.ymware.gateway.core.protocol;

import com.ymware.gateway.sdk.model.ProtocolType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Rerank 协议适配器（App 层）
 * <p>
 * 委托 SDK 的 {@link com.ymware.gateway.sdk.protocol.RerankProtocolAdapter}。
 * </p>
 */
@Component
public class RerankProtocolAdapter extends AbstractSseProtocolAdapter {

    private final com.ymware.gateway.sdk.protocol.RerankProtocolAdapter sdkAdapter;

    public RerankProtocolAdapter(ObjectMapper objectMapper,
                                  com.ymware.gateway.sdk.protocol.RerankProtocolAdapter sdkAdapter) {
        super(objectMapper);
        this.sdkAdapter = sdkAdapter;
    }

    @Override
    protected com.ymware.gateway.sdk.protocol.RerankProtocolAdapter sdkAdapter() {
        return sdkAdapter;
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.RERANK;
    }
}
