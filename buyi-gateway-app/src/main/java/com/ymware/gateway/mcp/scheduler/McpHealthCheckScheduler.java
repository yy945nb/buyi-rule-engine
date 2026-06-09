package com.ymware.gateway.mcp.scheduler;

import com.ymware.gateway.mcp.config.McpProperties;
import com.ymware.gateway.mcp.service.McpHealthCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpHealthCheckScheduler {

    private static final Logger log = LoggerFactory.getLogger(McpHealthCheckScheduler.class);

    private final McpHealthCheckService healthCheckService;
    private final McpProperties properties;

    public McpHealthCheckScheduler(McpHealthCheckService healthCheckService, McpProperties properties) {
        this.healthCheckService = healthCheckService;
        this.properties = properties;
    }

    @Scheduled(fixedDelayString = "#{${gateway.mcp.discovery.health-check-interval:30s}.toMillis()}")
    public void runHealthChecks() {
        try {
            healthCheckService.checkAllServices();
        } catch (Exception e) {
            log.error("Health check scheduler error: {}", e.getMessage());
        }
    }
}
