package com.ymware.gateway.mcp.routing;

import com.ymware.gateway.mcp.routing.mapper.ServiceCapabilityMapper;
import com.ymware.gateway.mcp.routing.model.ServiceCapabilityDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 服务能力注册中心。
 * 管理每个服务的能力标签、健康状态和响应时间。
 * 支持按能力标签查找服务，为路由规则引擎提供能力匹配。
 */
@Component
public class CapabilityRegistry {

    private static final Logger log = LoggerFactory.getLogger(CapabilityRegistry.class);

    private final ServiceCapabilityMapper capabilityMapper;

    // serviceId -> Set<capabilityTag>
    private final Map<String, Set<String>> capabilitiesByService = new ConcurrentHashMap<>();
    // capabilityTag -> List<ServiceCapabilityDO>
    private final Map<String, List<ServiceCapabilityDO>> servicesByCapability = new ConcurrentHashMap<>();

    public CapabilityRegistry(ServiceCapabilityMapper capabilityMapper) {
        this.capabilityMapper = capabilityMapper;
    }

    @PostConstruct
    public void init() {
        refresh();
    }

    /**
     * 刷新缓存，从数据库重新加载所有能力信息。
     */
    public void refresh() {
        try {
            List<ServiceCapabilityDO> all = capabilityMapper.findAll();

            Map<String, Set<String>> byService = new ConcurrentHashMap<>();
            Map<String, List<ServiceCapabilityDO>> byCapability = new ConcurrentHashMap<>();

            for (ServiceCapabilityDO cap : all) {
                byService.computeIfAbsent(cap.getServiceId(), k -> new HashSet<>())
                        .add(cap.getCapabilityTag());
                byCapability.computeIfAbsent(cap.getCapabilityTag(), k -> new ArrayList<>())
                        .add(cap);
            }

            capabilitiesByService.clear();
            capabilitiesByService.putAll(byService);
            servicesByCapability.clear();
            servicesByCapability.putAll(byCapability);

            log.info("Loaded {} service capabilities across {} services", all.size(), byService.size());
        } catch (Exception e) {
            log.error("Failed to refresh capability registry: {}", e.getMessage());
        }
    }

    /**
     * 获取服务的所有能力标签。
     */
    public Set<String> getCapabilities(String serviceId) {
        return capabilitiesByService.getOrDefault(serviceId, Collections.emptySet());
    }

    /**
     * 获取拥有指定能力的所有服务（健康状态）。
     */
    public List<ServiceCapabilityDO> getHealthyServices(String capabilityTag) {
        return servicesByCapability.getOrDefault(capabilityTag, Collections.emptyList()).stream()
                .filter(c -> Boolean.TRUE.equals(c.getHealthStatus()))
                .collect(Collectors.toList());
    }

    /**
     * 获取拥有指定能力的所有服务。
     */
    public List<ServiceCapabilityDO> getServices(String capabilityTag) {
        return servicesByCapability.getOrDefault(capabilityTag, Collections.emptyList());
    }

    /**
     * 注册服务能力。
     */
    public void register(ServiceCapabilityDO capability) {
        capabilityMapper.insert(capability);
        refresh();
    }

    /**
     * 更新服务健康状态。
     */
    public void updateHealth(String serviceId, String capabilityTag, boolean healthy) {
        capabilityMapper.updateHealthStatus(serviceId, capabilityTag, healthy);
        refresh();
    }

    /**
     * 更新服务平均响应时间。
     */
    public void updateResponseTime(String serviceId, String capabilityTag, long avgResponseTimeMs) {
        capabilityMapper.updateAvgResponseTime(serviceId, capabilityTag, avgResponseTimeMs);
    }

    /**
     * 检查服务是否具有指定能力。
     */
    public boolean hasCapability(String serviceId, String capabilityTag) {
        Set<String> caps = capabilitiesByService.get(serviceId);
        return caps != null && caps.contains(capabilityTag);
    }
}
