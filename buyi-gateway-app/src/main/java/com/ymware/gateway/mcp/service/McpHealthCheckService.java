package com.ymware.gateway.mcp.service;

import com.ymware.gateway.mcp.discovery.McpServiceDiscovery;
import com.ymware.gateway.mcp.discovery.McpServiceInfo;
import com.ymware.gateway.mcp.mapper.McpServiceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpHealthCheckService {

    private static final Logger log = LoggerFactory.getLogger(McpHealthCheckService.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final WebClient webClient;
    private final McpServiceMapper serviceMapper;
    private final McpServiceDiscovery serviceDiscovery;

    public McpHealthCheckService(WebClient.Builder webClientBuilder,
                                 McpServiceMapper serviceMapper,
                                 McpServiceDiscovery serviceDiscovery) {
        this.webClient = webClientBuilder.build();
        this.serviceMapper = serviceMapper;
        this.serviceDiscovery = serviceDiscovery;
    }

    public void checkAllServices() {
        List<McpServiceInfo> services = serviceDiscovery.getAllActiveServices();
        for (McpServiceInfo service : services) {
            checkService(service);
        }
    }

    public boolean checkService(McpServiceInfo service) {
        String healthUrl = service.getHealthCheckUrl();
        if (healthUrl == null || healthUrl.isEmpty()) {
            healthUrl = service.getEndpoint() + (service.getEndpoint().endsWith("/") ? "" : "/") + "health";
        }

        try {
            String responseBody = webClient.get()
                    .uri(healthUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(TIMEOUT)
                    .block();

            boolean healthy = responseBody != null && !responseBody.isEmpty();
            if (healthy) {
                updateServiceStatus(service.getServiceId(), "ACTIVE");
            } else {
                log.warn("Health check empty response for {}", service.getServiceId());
                updateServiceStatus(service.getServiceId(), "MAINTENANCE");
            }
            return healthy;
        } catch (Exception e) {
            log.warn("Health check failed for {}: {}", service.getServiceId(), e.getMessage());
            updateServiceStatus(service.getServiceId(), "MAINTENANCE");
            return false;
        }
    }

    private void updateServiceStatus(String serviceId, String status) {
        try {
            var record = serviceMapper.findByServiceId(serviceId);
            if (record != null && !status.equals(record.getStatus())) {
                record.setStatus(status);
                serviceMapper.update(record);
                serviceDiscovery.invalidate(serviceId);
                log.info("Service {} status updated to {}", serviceId, status);
            }
        } catch (Exception e) {
            log.error("Failed to update service status for {}: {}", serviceId, e.getMessage());
        }
    }
}
