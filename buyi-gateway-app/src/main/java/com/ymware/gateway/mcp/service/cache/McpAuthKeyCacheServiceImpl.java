package com.ymware.gateway.mcp.service.cache;

import com.ymware.gateway.mcp.auth.McpAuthKeyInfo;
import com.ymware.gateway.mcp.config.McpRedisConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class McpAuthKeyCacheServiceImpl implements McpAuthKeyCacheService {

    private static final Logger log = LoggerFactory.getLogger(McpAuthKeyCacheServiceImpl.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);
    private static final Duration NEGATIVE_TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public McpAuthKeyCacheServiceImpl(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<McpAuthKeyInfo> getAuthKey(String keyHash) {
        try {
            String key = McpRedisConstants.AUTH_KEY + keyHash;
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) {
                return Optional.empty();
            }
            if ("__INVALID__".equals(json)) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, McpAuthKeyInfo.class));
        } catch (Exception e) {
            log.warn("Failed to get auth key cache: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void putAuthKey(McpAuthKeyInfo authKey) {
        try {
            String key = McpRedisConstants.AUTH_KEY + authKey.getKeyHash();
            String json = objectMapper.writeValueAsString(authKey);
            redisTemplate.opsForValue().set(key, json, CACHE_TTL);
        } catch (Exception e) {
            log.warn("Failed to cache auth key: {}", e.getMessage());
        }
    }

    @Override
    public void evictAuthKey(String keyHash) {
        try {
            redisTemplate.delete(McpRedisConstants.AUTH_KEY + keyHash);
            redisTemplate.delete(McpRedisConstants.AUTH_KEY_STATUS + keyHash);
        } catch (Exception e) {
            log.warn("Failed to evict auth key cache: {}", e.getMessage());
        }
    }

    @Override
    public boolean isKeyInvalid(String keyHash) {
        try {
            String key = McpRedisConstants.AUTH_KEY_STATUS + keyHash;
            String val = redisTemplate.opsForValue().get(key);
            return "INVALID".equals(val);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void markKeyInvalid(String keyHash) {
        try {
            String key = McpRedisConstants.AUTH_KEY_STATUS + keyHash;
            redisTemplate.opsForValue().set(key, "INVALID", NEGATIVE_TTL);
        } catch (Exception e) {
            log.warn("Failed to mark key invalid: {}", e.getMessage());
        }
    }

    @Override
    public void evictAll() {
        try {
            var keys = redisTemplate.keys(McpRedisConstants.AUTH_KEY + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            var statusKeys = redisTemplate.keys(McpRedisConstants.AUTH_KEY_STATUS + "*");
            if (statusKeys != null && !statusKeys.isEmpty()) {
                redisTemplate.delete(statusKeys);
            }
        } catch (Exception e) {
            log.warn("Failed to evict all auth key cache: {}", e.getMessage());
        }
    }
}
