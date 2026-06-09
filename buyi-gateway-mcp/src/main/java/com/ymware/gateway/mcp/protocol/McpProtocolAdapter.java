package com.ymware.gateway.mcp.protocol;

import java.util.List;

/**
 * MCP protocol adapter interface (SDK layer).
 * Handles JSON-RPC parsing and response encoding for MCP protocol.
 */
public interface McpProtocolAdapter {

    /**
     * Parse raw JSON-RPC body into McpRequest.
     */
    McpRequest parseRequest(String rawBody);

    /**
     * Build initialize result with server capabilities and tool list.
     */
    McpResponse buildInitializeResult(McpRequest request, List<McpToolDefinition> tools);

    /**
     * Build tools/list result.
     */
    McpResponse buildToolsListResult(McpRequest request, List<McpToolDefinition> tools);

    /**
     * Build tools/call result.
     */
    McpResponse buildToolCallResult(McpRequest request, McpToolCallResult callResult);

    /**
     * Build SSE event string from McpResponse.
     */
    String encodeSseEvent(McpResponse response);

    /**
     * Build error response.
     */
    McpResponse buildError(Object id, int code, String message);
}
