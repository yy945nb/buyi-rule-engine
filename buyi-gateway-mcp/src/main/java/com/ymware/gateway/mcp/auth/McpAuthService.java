package com.ymware.gateway.mcp.auth;

/**
 * MCP authentication service interface.
 * Supports Redis-first + MySQL fallback validation.
 */
public interface McpAuthService {

    /**
     * Validate an auth key. Returns key info if valid, null if invalid.
     */
    McpAuthKeyInfo validateAuthKey(String authKey);

    /**
     * Check if a path is whitelisted (bypasses auth).
     */
    boolean isWhitelistedPath(String path);

    /**
     * Update last used time for a key (async).
     */
    void updateLastUsedTime(String authKey);
}
