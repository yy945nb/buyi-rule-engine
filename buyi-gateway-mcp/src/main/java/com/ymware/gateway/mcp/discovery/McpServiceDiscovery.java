package com.ymware.gateway.mcp.discovery;

import java.util.List;

/**
 * MCP service discovery interface. Supports Nacos + Redis + MySQL three-tier lookup.
 */
public interface McpServiceDiscovery {

    /**
     * Get service info by serviceId. Three-tier lookup: Redis -> Nacos -> MySQL.
     */
    McpServiceInfo getService(String serviceId);

    /**
     * Get all active services.
     */
    List<McpServiceInfo> getAllActiveServices();

    /**
     * Check if a service is active.
     */
    boolean isServiceActive(String serviceId);

    /**
     * Invalidate cache for a specific service.
     */
    void invalidate(String serviceId);

    /**
     * Refresh all service caches.
     */
    void refreshAll();
}
