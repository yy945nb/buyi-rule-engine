package com.ymware.gateway.mcp.service.cache;

import com.ymware.gateway.mcp.auth.McpAuthKeyInfo;

import java.util.Optional;

public interface McpAuthKeyCacheService {

    Optional<McpAuthKeyInfo> getAuthKey(String keyHash);

    void putAuthKey(McpAuthKeyInfo authKey);

    void evictAuthKey(String keyHash);

    boolean isKeyInvalid(String keyHash);

    void markKeyInvalid(String keyHash);

    void evictAll();
}
