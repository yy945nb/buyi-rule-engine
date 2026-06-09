package com.ymware.gateway.mcp.service.cache;

import com.ymware.gateway.mcp.stats.McpServiceStats;

import java.util.Map;

public interface McpStatsCacheService {

    void incrementTotalCalls(String serviceId);

    void incrementSuccessCalls(String serviceId);

    void incrementFailedCalls(String serviceId);

    void recordResponseTime(String serviceId, long responseTimeMs);

    void recordUniqueUser(String serviceId, String userId);

    McpServiceStats getRealtimeStats(String serviceId);

    Map<String, McpServiceStats> getAllRealtimeStats();

    void clearStats(String serviceId);

    void clearAllStats();
}
