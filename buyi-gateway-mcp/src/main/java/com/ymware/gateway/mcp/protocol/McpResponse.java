package com.ymware.gateway.mcp.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP JSON-RPC response envelope.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpResponse {

    @Builder.Default
    private String jsonrpc = "2.0";

    private Object id;
    private JsonNode result;
    private McpError error;

    public boolean isError() {
        return error != null;
    }

    public static McpResponse success(Object id, JsonNode result) {
        return McpResponse.builder().id(id).result(result).build();
    }

    public static McpResponse error(Object id, int code, String message) {
        return McpResponse.builder()
                .id(id)
                .error(McpError.builder().code(code).message(message).build())
                .build();
    }

    public static McpResponse error(Object id, McpError error) {
        return McpResponse.builder().id(id).error(error).build();
    }
}
