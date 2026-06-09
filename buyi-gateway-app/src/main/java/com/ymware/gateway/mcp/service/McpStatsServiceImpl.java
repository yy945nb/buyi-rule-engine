package com.ymware.gateway.mcp.service;

import com.ymware.gateway.mcp.stats.McpServiceStats;
import com.ymware.gateway.mcp.stats.McpStatsCollector;
import com.ymware.gateway.mcp.service.cache.McpStatsCacheService;
import com.ymware.gateway.mcp.mapper.McpServiceStatsMapper;
import com.ymware.gateway.mcp.model.McpServiceStatsDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpStatsServiceImpl implements McpStatsCollector {

    private static final Logger log = LoggerFactory.getLogger(McpStatsServiceImpl.class);

    private final McpStatsCacheService cacheService;
    private final McpServiceStatsMapper statsMapper;

    public McpStatsServiceImpl(McpStatsCacheService cacheService, McpServiceStatsMapper statsMapper) {
        this.cacheService = cacheService;
        this.statsMapper = statsMapper;
    }

    @Override
    public void recordRequest(String serviceId, String userId, boolean success, long responseTimeMs) {
        cacheService.incrementTotalCalls(serviceId);
        if (success) {
            cacheService.incrementSuccessCalls(serviceId);
        } else {
            cacheService.incrementFailedCalls(serviceId);
        }
        cacheService.recordResponseTime(serviceId, responseTimeMs);
        if (userId != null) {
            cacheService.recordUniqueUser(serviceId, userId);
        }
    }

    @Override
    public McpServiceStats getRealtimeStats(String serviceId) {
        return cacheService.getRealtimeStats(serviceId);
    }

    @Override
    public McpServiceStats getHistoricalStats(String serviceId) {
        McpServiceStatsDO record = statsMapper.findByServiceIdAndDate(serviceId, java.time.LocalDate.now());
        if (record == null) {
            McpServiceStats empty = new McpServiceStats();
            empty.setServiceId(serviceId);
            return empty;
        }
        McpServiceStats stats = new McpServiceStats();
        stats.setServiceId(record.getServiceId());
        stats.setTotalCalls(record.getTotalCalls());
        stats.setSuccessCalls(record.getSuccessCalls());
        stats.setFailedCalls(record.getFailedCalls());
        stats.setAvgResponseTimeMs(record.getAvgResponseTimeMs());
        stats.setMaxResponseTimeMs(record.getMaxResponseTimeMs());
        stats.setUniqueUsers(record.getUniqueUsers());
        return stats;
    }

    public Map<String, McpServiceStats> getAllRealtimeStats() {
        return cacheService.getAllRealtimeStats();
    }

    @Override
    public void flushToDatabase() {
        Map<String, McpServiceStats> allStats = cacheService.getAllRealtimeStats();
        for (Map.Entry<String, McpServiceStats> entry : allStats.entrySet()) {
            McpServiceStats stats = entry.getValue();
            if (stats.getTotalCalls() == 0) continue;

            McpServiceStatsDO record = new McpServiceStatsDO();
            record.setServiceId(stats.getServiceId());
            record.setDateKey(LocalDate.now());
            record.setTotalCalls((int) stats.getTotalCalls());
            record.setSuccessCalls((int) stats.getSuccessCalls());
            record.setFailedCalls((int) stats.getFailedCalls());
            record.setAvgResponseTimeMs((int) stats.getAvgResponseTimeMs());
            record.setMaxResponseTimeMs((int) stats.getMaxResponseTimeMs());
            record.setUniqueUsers((int) stats.getUniqueUsers());

            try {
                statsMapper.insertOrUpdate(record);
            } catch (Exception e) {
                log.error("Failed to flush stats for {}: {}", stats.getServiceId(), e.getMessage());
            }
        }
        log.info("Flushed stats for {} services to database", allStats.size());
    }

    @Override
    public void clearStats(String serviceId) {
        cacheService.clearStats(serviceId);
    }
}
