package com.ymware.gateway.mcp.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP tools/call request parameters.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpToolCallParams {

    private String name;
    private JsonNode arguments;
}
