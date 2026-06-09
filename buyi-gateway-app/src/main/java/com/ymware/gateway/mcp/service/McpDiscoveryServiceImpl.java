package com.ymware.gateway.mcp.service;

import com.ymware.gateway.mcp.discovery.McpServiceDiscovery;
import com.ymware.gateway.mcp.discovery.McpServiceInfo;
import com.ymware.gateway.mcp.mapper.McpServiceMapper;
import com.ymware.gateway.mcp.model.McpServiceDO;
import com.ymware.gateway.mcp.service.cache.McpServiceCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpDiscoveryServiceImpl implements McpServiceDiscovery {

    private static final Logger log = LoggerFactory.getLogger(McpDiscoveryServiceImpl.class);
    private static final String MCP_METADATA_TAG = "mcp";

    private final McpServiceCacheService cacheService;
    private final McpServiceMapper serviceMapper;
    private final DiscoveryClient discoveryClient;

    public McpDiscoveryServiceImpl(McpServiceCacheService cacheService,
                                   McpServiceMapper serviceMapper,
                                   @Autowired(required = false) DiscoveryClient discoveryClient) {
        this.cacheService = cacheService;
        this.serviceMapper = serviceMapper;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public McpServiceInfo getService(String serviceId) {
        // 1. Redis cache
        Optional<McpServiceInfo> cached = cacheService.getService(serviceId);
        if (cached.isPresent()) {
            return cached.get();
        }

        // 2. Nacos discovery
        McpServiceInfo fromNacos = discoverFromNacos(serviceId);
        if (fromNacos != null) {
            cacheService.putService(fromNacos);
            return fromNacos;
        }

        // 3. MySQL fallback
        McpServiceInfo fromDb = discoverFromDatabase(serviceId);
        if (fromDb != null) {
            cacheService.putService(fromDb);
            return fromDb;
        }

        return null;
    }

    @Override
    public List<McpServiceInfo> getAllActiveServices() {
        List<McpServiceDO> services = serviceMapper.findByStatus("ACTIVE");
        return services.stream()
                .map(this::convertToServiceInfo)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isServiceActive(String serviceId) {
        McpServiceInfo info = getService(serviceId);
        return info != null && info.isActive();
    }

    @Override
    public void invalidate(String serviceId) {
        cacheService.evictService(serviceId);
    }

    @Override
    public void refreshAll() {
        cacheService.evictAll();
        log.info("MCP service cache cleared, will reload on next access");
    }

    private McpServiceInfo discoverFromNacos(String serviceId) {
        if (discoveryClient == null) {
            return null;
        }
        try {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            if (instances == null || instances.isEmpty()) {
                return null;
            }
            ServiceInstance instance = instances.get(0);
            // Check if this is a registered MCP service via metadata
            String mcpTag = instance.getMetadata().get(MCP_METADATA_TAG);
            if (!"true".equals(mcpTag)) {
                return null;
            }
            String serviceType = instance.getMetadata().getOrDefault("service-type", "TRANSPARENT");
            return McpServiceInfo.builder()
                    .serviceId(serviceId)
                    .name(instance.getInstanceId())
                    .endpoint(instance.getUri().toString())
                    .serviceType(McpServiceInfo.ServiceType.valueOf(serviceType))
                    .status(McpServiceInfo.ServiceStatus.ACTIVE)
                    .build();
        } catch (Exception e) {
            log.debug("Nacos discovery failed for {}: {}", serviceId, e.getMessage());
            return null;
        }
    }

    private McpServiceInfo discoverFromDatabase(String serviceId) {
        McpServiceDO record = serviceMapper.findByServiceId(serviceId);
        if (record == null) {
            return null;
        }
        return convertToServiceInfo(record);
    }

    private McpServiceInfo convertToServiceInfo(McpServiceDO record) {
        return McpServiceInfo.builder()
                .serviceId(record.getServiceId())
                .name(record.getName())
                .description(record.getDescription())
                .endpoint(record.getEndpoint())
                .serviceType(McpServiceInfo.ServiceType.valueOf(record.getServiceType()))
                .status(McpServiceInfo.ServiceStatus.valueOf(record.getStatus()))
                .maxQps(record.getMaxQps())
                .healthCheckUrl(record.getHealthCheckUrl())
                .nacosServiceId(record.getNacosServiceId())
                .build();
    }
}
