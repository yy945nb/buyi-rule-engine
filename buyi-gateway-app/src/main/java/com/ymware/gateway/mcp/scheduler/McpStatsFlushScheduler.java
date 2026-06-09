package com.ymware.gateway.mcp.scheduler;

import com.ymware.gateway.mcp.config.McpProperties;
import com.ymware.gateway.mcp.stats.McpStatsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpStatsFlushScheduler {

    private static final Logger log = LoggerFactory.getLogger(McpStatsFlushScheduler.class);

    private final McpStatsCollector statsCollector;
    private final McpProperties properties;

    public McpStatsFlushScheduler(McpStatsCollector statsCollector, McpProperties properties) {
        this.statsCollector = statsCollector;
        this.properties = properties;
    }

    @Scheduled(fixedDelayString = "#{${gateway.mcp.stats.flush-interval:5m}.toMillis()}")
    public void flushStats() {
        if (!properties.getStats().isEnabled()) {
            return;
        }
        try {
            statsCollector.flushToDatabase();
        } catch (Exception e) {
            log.error("Stats flush failed: {}", e.getMessage());
        }
    }
}
