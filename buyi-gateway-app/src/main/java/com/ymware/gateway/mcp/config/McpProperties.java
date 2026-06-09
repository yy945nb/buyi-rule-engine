package com.ymware.gateway.mcp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * MCP Gateway configuration properties.
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.mcp")
public class McpProperties {

    private boolean enabled = true;

    private ProxyProperties proxy = new ProxyProperties();

    private AuthProperties auth = new AuthProperties();

    private StatsProperties stats = new StatsProperties();

    private DiscoveryProperties discovery = new DiscoveryProperties();

    @Data
    public static class ProxyProperties {
        private Duration timeout = Duration.ofSeconds(300);
        private int maxInMemorySize = 256 * 1024;
        private Duration connectTimeout = Duration.ofSeconds(5);
        private Duration readTimeout = Duration.ofSeconds(30);
    }

    @Data
    public static class AuthProperties {
        private boolean enabled = true;
        private AuthType authType = AuthType.DB;
        private Set<String> staticKeys = Set.of();
        private List<String> whitelist = List.of("/mcp/health", "/actuator/**");
    }

    @Data
    public static class StatsProperties {
        private boolean enabled = true;
        private Duration flushInterval = Duration.ofMinutes(5);
    }

    @Data
    public static class DiscoveryProperties {
        private boolean nacosEnabled = true;
        private Duration cacheExpire = Duration.ofMinutes(30);
        private Duration healthCheckInterval = Duration.ofSeconds(30);
    }

    public enum AuthType {
        STATIC, DB
    }
}
