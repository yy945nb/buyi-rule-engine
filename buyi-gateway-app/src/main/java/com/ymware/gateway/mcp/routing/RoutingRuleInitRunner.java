package com.ymware.gateway.mcp.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 应用启动时，将 YAML 配置的路由规则同步到数据库。
 */
@Component
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class RoutingRuleInitRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RoutingRuleInitRunner.class);

    private final YamlRuleConfigLoader configLoader;
    private final CapabilityRegistry capabilityRegistry;

    public RoutingRuleInitRunner(YamlRuleConfigLoader configLoader, CapabilityRegistry capabilityRegistry) {
        this.configLoader = configLoader;
        this.capabilityRegistry = capabilityRegistry;
    }

    @Override
    public void run(String... args) {
        try {
            configLoader.syncToDatabase();
            capabilityRegistry.refresh();
            log.info("Routing rule engine initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize routing rule engine: {}", e.getMessage());
        }
    }
}
