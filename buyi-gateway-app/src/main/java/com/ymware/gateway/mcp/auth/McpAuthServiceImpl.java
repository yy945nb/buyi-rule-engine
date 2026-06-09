package com.ymware.gateway.mcp.auth;

import com.ymware.gateway.mcp.config.McpProperties;
import com.ymware.gateway.mcp.mapper.McpAuthKeyMapper;
import com.ymware.gateway.mcp.model.McpAuthKeyDO;
import com.ymware.gateway.mcp.service.cache.McpAuthKeyCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpAuthServiceImpl implements McpAuthService {

    private static final Logger log = LoggerFactory.getLogger(McpAuthServiceImpl.class);

    private final McpAuthKeyCacheService cacheService;
    private final McpAuthKeyMapper authKeyMapper;
    private final McpProperties properties;

    public McpAuthServiceImpl(McpAuthKeyCacheService cacheService,
                              McpAuthKeyMapper authKeyMapper,
                              McpProperties properties) {
        this.cacheService = cacheService;
        this.authKeyMapper = authKeyMapper;
        this.properties = properties;
    }

    @Override
    public McpAuthKeyInfo validateAuthKey(String rawKey) {
        if (rawKey == null || rawKey.isBlank()) {
            return null;
        }

        // Check static keys first (if configured)
        McpAuthKeyInfo staticResult = checkStaticKeys(rawKey);
        if (staticResult != null) {
            return staticResult;
        }

        // DB mode
        if (properties.getAuth().getAuthType() != McpProperties.AuthType.DB) {
            return null;
        }

        String keyHash = hashKey(rawKey);

        // Negative cache: fast reject known-invalid keys
        if (cacheService.isKeyInvalid(keyHash)) {
            return null;
        }

        // Positive cache
        var cached = cacheService.getAuthKey(keyHash);
        if (cached.isPresent()) {
            McpAuthKeyInfo info = cached.get();
            return info.isValid() ? info : null;
        }

        // MySQL fallback
        McpAuthKeyDO record = authKeyMapper.findByKeyHash(keyHash);
        if (record == null) {
            cacheService.markKeyInvalid(keyHash);
            return null;
        }

        McpAuthKeyInfo info = convertToInfo(record);
        if (!info.isValid()) {
            cacheService.markKeyInvalid(keyHash);
            return null;
        }

        cacheService.putAuthKey(info);
        return info;
    }

    @Override
    public boolean isWhitelistedPath(String path) {
        List<String> whitelist = properties.getAuth().getWhitelist();
        if (whitelist == null || whitelist.isEmpty()) {
            return false;
        }
        for (String pattern : whitelist) {
            if (matchPath(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateLastUsedTime(String authKey) {
        // No-op for static keys
        if (authKey == null || authKey.startsWith("__static__")) {
            return;
        }
        try {
            String keyHash = hashKey(authKey);
            McpAuthKeyDO record = authKeyMapper.findByKeyHash(keyHash);
            if (record != null) {
                authKeyMapper.updateLastUsedTime(record.getId(), java.time.LocalDateTime.now());
            }
        } catch (Exception e) {
            log.warn("Failed to update last used time: {}", e.getMessage());
        }
    }

    private McpAuthKeyInfo checkStaticKeys(String rawKey) {
        var staticKeys = properties.getAuth().getStaticKeys();
        if (staticKeys == null || staticKeys.isEmpty()) {
            return null;
        }
        for (String key : staticKeys) {
            if (key.equals(rawKey)) {
                McpAuthKeyInfo info = new McpAuthKeyInfo();
                info.setKeyHash("__static__");
                info.setActive(true);
                info.setUserId("static-user");
                info.setServiceId("*");
                return info;
            }
        }
        return null;
    }

    private boolean matchPath(String pattern, String path) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return path.startsWith(prefix) && !path.substring(prefix.length()).contains("/");
        }
        return pattern.equals(path);
    }

    private String hashKey(String rawKey) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private McpAuthKeyInfo convertToInfo(McpAuthKeyDO record) {
        return McpAuthKeyInfo.builder()
                .id(record.getId())
                .keyHash(record.getKeyHash())
                .keyPrefix(record.getKeyPrefix())
                .userId(record.getUserId())
                .serviceId(record.getServiceId())
                .expiresAt(record.getExpiresAt())
                .isActive(record.getIsActive() != null && record.getIsActive())
                .lastUsedAt(record.getLastUsedAt())
                .build();
    }
}
