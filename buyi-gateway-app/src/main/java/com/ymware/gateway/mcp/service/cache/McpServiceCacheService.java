package com.ymware.gateway.mcp.service.cache;

import com.ymware.gateway.mcp.discovery.McpServiceInfo;

import java.util.Optional;

public interface McpServiceCacheService {

    Optional<McpServiceInfo> getService(String serviceId);

    void putService(McpServiceInfo service);

    void evictService(String serviceId);

    void evictAll();
}
