package com.ymware.gateway.mcp.stats;

/**
 * MCP statistics collector interface.
 * Records request stats in Redis with periodic flush to MySQL.
 */
public interface McpStatsCollector {

    /**
     * Record a request (success or failure).
     */
    void recordRequest(String serviceId, String userId, boolean success, long responseTimeMs);

    /**
     * Get real-time stats from Redis.
     */
    McpServiceStats getRealtimeStats(String serviceId);

    /**
     * Get historical stats from MySQL.
     */
    McpServiceStats getHistoricalStats(String serviceId);

    /**
     * Flush Redis stats to MySQL.
     */
    void flushToDatabase();

    /**
     * Clear all stats for a service.
     */
    void clearStats(String serviceId);
}
