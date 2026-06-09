package com.ymware.gateway.mcp.config;

/**
 * Redis key constants for MCP Gateway.
 * All keys use "mcp:" prefix to avoid collision with AI-Gateway's existing keys.
 */
public final class McpRedisConstants {

    private McpRedisConstants() {}

    public static final String PREFIX = "mcp:";

    /** Service entity cache: mcp:service:cache:{serviceId} */
    public static final String SERVICE_CACHE = PREFIX + "service:cache:";

    /** Active service ID set: mcp:service:active:set */
    public static final String ACTIVE_SERVICES_SET = PREFIX + "service:active:set";

    /** Valid auth key cache: mcp:auth:key:{keyHash} */
    public static final String AUTH_KEY = PREFIX + "auth:key:";

    /** Invalid auth key negative cache: mcp:auth:status:{keyHash} */
    public static final String AUTH_KEY_STATUS = PREFIX + "auth:status:";

    /** Per-service per-day stats hash: mcp:stats:service:{serviceId}:{date} */
    public static final String STATS_SERVICE = PREFIX + "stats:service:";

    /** Per-service per-day unique users: mcp:stats:users:{serviceId}:{date} */
    public static final String STATS_USERS = PREFIX + "stats:users:";

    /** Service cache TTL in minutes */
    public static final long SERVICE_CACHE_TTL_MINUTES = 30;

    /** Auth key cache TTL in minutes */
    public static final long AUTH_KEY_CACHE_TTL_MINUTES = 30;
}
