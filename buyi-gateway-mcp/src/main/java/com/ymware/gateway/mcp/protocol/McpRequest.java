package com.ymware.gateway.mcp.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP JSON-RPC request envelope.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpRequest {

    @Builder.Default
    private String jsonrpc = "2.0";

    private Object id;
    private String method;
    private JsonNode params;

    public boolean hasId() {
        return id != null;
    }
}
