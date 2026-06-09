package com.ymware.gateway.mcp.registry;

import com.ymware.gateway.mcp.protocol.McpToolCallResult;
import com.ymware.gateway.mcp.protocol.McpToolDefinition;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Tool executor interface. Executes MCP tool calls by forwarding to downstream REST APIs.
 */
public interface ToolExecutor {

    /**
     * Execute a tool call by forwarding to the REST endpoint.
     *
     * @param tool      the tool definition with REST mapping
     * @param arguments the MCP tool call arguments
     * @return the MCP tool call result
     */
    McpToolCallResult execute(McpToolDefinition tool, JsonNode arguments);
}
