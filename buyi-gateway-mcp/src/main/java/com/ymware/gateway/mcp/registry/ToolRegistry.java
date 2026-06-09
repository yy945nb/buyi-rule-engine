package com.ymware.gateway.mcp.registry;

import com.ymware.gateway.mcp.protocol.McpToolDefinition;

import java.util.List;

/**
 * Tool registry interface. Manages MCP tool definitions for REST->MCP conversion.
 */
public interface ToolRegistry {

    /**
     * Get all tools for a service.
     */
    List<McpToolDefinition> getToolsForService(String serviceId);

    /**
     * Get a specific tool by name within a service.
     */
    McpToolDefinition getTool(String serviceId, String toolName);

    /**
     * Register a tool.
     */
    void registerTool(String serviceId, McpToolDefinition tool);

    /**
     * Remove a tool.
     */
    void removeTool(String serviceId, String toolName);

    /**
     * Reload tools from persistence.
     */
    void refresh();
}
