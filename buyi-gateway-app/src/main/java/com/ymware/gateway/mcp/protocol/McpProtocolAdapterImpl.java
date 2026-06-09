package com.ymware.gateway.mcp.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpProtocolAdapterImpl extends AbstractMcpProtocolAdapter {

    public McpProtocolAdapterImpl(ObjectMapper objectMapper) {
        super(objectMapper);
    }
}
