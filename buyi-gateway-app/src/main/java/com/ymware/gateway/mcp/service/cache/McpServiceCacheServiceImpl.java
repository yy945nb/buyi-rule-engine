package com.ymware.gateway.mcp.service.cache;

import com.ymware.gateway.mcp.config.McpRedisConstants;
import com.ymware.gateway.mcp.discovery.McpServiceInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class McpServiceCacheServiceImpl implements McpServiceCacheService {

    private static final Logger log = LoggerFactory.getLogger(McpServiceCacheServiceImpl.class);
    private static final Duration TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public McpServiceCacheServiceImpl(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<McpServiceInfo> getService(String serviceId) {
        try {
            String key = McpRedisConstants.SERVICE_CACHE + serviceId;
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, McpServiceInfo.class));
        } catch (Exception e) {
            log.warn("Failed to get service cache for {}: {}", serviceId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void putService(McpServiceInfo service) {
        try {
            String key = McpRedisConstants.SERVICE_CACHE + service.getServiceId();
            String json = objectMapper.writeValueAsString(service);
            redisTemplate.opsForValue().set(key, json, TTL);
        } catch (Exception e) {
            log.warn("Failed to cache service {}: {}", service.getServiceId(), e.getMessage());
        }
    }

    @Override
    public void evictService(String serviceId) {
        try {
            redisTemplate.delete(McpRedisConstants.SERVICE_CACHE + serviceId);
        } catch (Exception e) {
            log.warn("Failed to evict service cache for {}: {}", serviceId, e.getMessage());
        }
    }

    @Override
    public void evictAll() {
        try {
            var keys = redisTemplate.keys(McpRedisConstants.SERVICE_CACHE + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.warn("Failed to evict all service cache: {}", e.getMessage());
        }
    }
}
