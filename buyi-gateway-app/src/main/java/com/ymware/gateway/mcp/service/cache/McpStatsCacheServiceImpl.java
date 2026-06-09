package com.ymware.gateway.mcp.service.cache;

import com.ymware.gateway.mcp.config.McpRedisConstants;
import com.ymware.gateway.mcp.stats.McpServiceStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class McpStatsCacheServiceImpl implements McpStatsCacheService {

    private static final Logger log = LoggerFactory.getLogger(McpStatsCacheServiceImpl.class);

    private final StringRedisTemplate redisTemplate;

    public McpStatsCacheServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String statsKey(String serviceId) {
        return McpRedisConstants.STATS_SERVICE + serviceId + ":" + LocalDate.now();
    }

    private String usersKey(String serviceId) {
        return McpRedisConstants.STATS_USERS + serviceId + ":" + LocalDate.now();
    }

    @Override
    public void incrementTotalCalls(String serviceId) {
        try {
            redisTemplate.opsForValue().increment(statsKey(serviceId) + ":total", 1);
        } catch (Exception e) {
            log.warn("Failed to increment total calls: {}", e.getMessage());
        }
    }

    @Override
    public void incrementSuccessCalls(String serviceId) {
        try {
            redisTemplate.opsForValue().increment(statsKey(serviceId) + ":success", 1);
        } catch (Exception e) {
            log.warn("Failed to increment success calls: {}", e.getMessage());
        }
    }

    @Override
    public void incrementFailedCalls(String serviceId) {
        try {
            redisTemplate.opsForValue().increment(statsKey(serviceId) + ":failed", 1);
        } catch (Exception e) {
            log.warn("Failed to increment failed calls: {}", e.getMessage());
        }
    }

    @Override
    public void recordResponseTime(String serviceId, long responseTimeMs) {
        try {
            String key = statsKey(serviceId) + ":totalTime";
            redisTemplate.opsForValue().increment(key, responseTimeMs);
        } catch (Exception e) {
            log.warn("Failed to record response time: {}", e.getMessage());
        }
    }

    @Override
    public void recordUniqueUser(String serviceId, String userId) {
        try {
            redisTemplate.opsForSet().add(usersKey(serviceId), userId);
        } catch (Exception e) {
            log.warn("Failed to record unique user: {}", e.getMessage());
        }
    }

    @Override
    public McpServiceStats getRealtimeStats(String serviceId) {
        try {
            String key = statsKey(serviceId);
            String totalStr = redisTemplate.opsForValue().get(key + ":total");
            String successStr = redisTemplate.opsForValue().get(key + ":success");
            String failedStr = redisTemplate.opsForValue().get(key + ":failed");
            String totalTimeStr = redisTemplate.opsForValue().get(key + ":totalTime");
            Long uniqueUsers = redisTemplate.opsForSet().size(usersKey(serviceId));

            int total = totalStr != null ? Integer.parseInt(totalStr) : 0;
            int success = successStr != null ? Integer.parseInt(successStr) : 0;
            int failed = failedStr != null ? Integer.parseInt(failedStr) : 0;
            long totalTime = totalTimeStr != null ? Long.parseLong(totalTimeStr) : 0;
            int avgTime = total > 0 ? (int) (totalTime / total) : 0;

            McpServiceStats stats = new McpServiceStats();
            stats.setServiceId(serviceId);
            stats.setTotalCalls(total);
            stats.setSuccessCalls(success);
            stats.setFailedCalls(failed);
            stats.setAvgResponseTimeMs(avgTime);
            stats.setMaxResponseTimeMs(0);
            stats.setUniqueUsers(uniqueUsers != null ? uniqueUsers.intValue() : 0);
            stats.setLastCallTime(LocalDateTime.now());
            return stats;
        } catch (Exception e) {
            log.warn("Failed to get realtime stats for {}: {}", serviceId, e.getMessage());
            McpServiceStats empty = new McpServiceStats();
            empty.setServiceId(serviceId);
            return empty;
        }
    }

    @Override
    public Map<String, McpServiceStats> getAllRealtimeStats() {
        Map<String, McpServiceStats> result = new HashMap<>();
        try {
            Set<String> keys = redisTemplate.keys(McpRedisConstants.STATS_SERVICE + "*:total");
            if (keys == null) return result;
            for (String key : keys) {
                // Extract serviceId from key: mcp:stats:service:{serviceId}:{date}:total
                String prefix = McpRedisConstants.STATS_SERVICE;
                String suffix = ":total";
                String remainder = key.substring(prefix.length());
                String serviceId = remainder.substring(0, remainder.indexOf(':'));
                result.put(serviceId, getRealtimeStats(serviceId));
            }
        } catch (Exception e) {
            log.warn("Failed to get all realtime stats: {}", e.getMessage());
        }
        return result;
    }

    @Override
    public void clearStats(String serviceId) {
        try {
            String key = statsKey(serviceId);
            redisTemplate.delete(key + ":total");
            redisTemplate.delete(key + ":success");
            redisTemplate.delete(key + ":failed");
            redisTemplate.delete(key + ":totalTime");
            redisTemplate.delete(usersKey(serviceId));
        } catch (Exception e) {
            log.warn("Failed to clear stats for {}: {}", serviceId, e.getMessage());
        }
    }

    @Override
    public void clearAllStats() {
        try {
            Set<String> keys = redisTemplate.keys(McpRedisConstants.STATS_SERVICE + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            Set<String> userKeys = redisTemplate.keys(McpRedisConstants.STATS_USERS + "*");
            if (userKeys != null && !userKeys.isEmpty()) {
                redisTemplate.delete(userKeys);
            }
        } catch (Exception e) {
            log.warn("Failed to clear all stats: {}", e.getMessage());
        }
    }
}
