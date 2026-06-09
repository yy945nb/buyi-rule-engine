package com.ymware.gateway.mcp.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP initialize request parameters.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpInitializeParams {

    private String protocolVersion;
    private ClientInfo clientInfo;
    private JsonNode capabilities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientInfo {
        private String name;
        private String version;
    }
}
