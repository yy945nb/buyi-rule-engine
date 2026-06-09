package com.ymware.gateway.mcp.routing;

import com.ymware.gateway.mcp.routing.model.RuleTarget;
import com.ymware.gateway.mcp.routing.model.ServiceCapabilityDO;
import com.ymware.gateway.mcp.routing.mapper.ServiceCapabilityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 目标选择器。
 * 从候选目标中选出最佳服务实例，支持：
 * - 加权随机选择
 * - 健康状态过滤
 * - 响应时间择优
 * - fallback 降级
 */
@Component
public class TargetSelector {

    private static final Logger log = LoggerFactory.getLogger(TargetSelector.class);

    private final ServiceCapabilityMapper capabilityMapper;

    public TargetSelector(ServiceCapabilityMapper capabilityMapper) {
        this.capabilityMapper = capabilityMapper;
    }

    /**
     * 从候选列表中选择最佳目标。
     *
     * @param candidates 候选目标列表
     * @return 选定的目标，如果没有可用目标返回 null
     */
    public SelectResult select(List<RuleTarget> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return new SelectResult(null, "no candidates");
        }

        // 分离主目标和 fallback
        List<RuleTarget> primary = candidates.stream()
                .filter(t -> !t.isFallback())
                .collect(Collectors.toList());
        List<RuleTarget> fallbacks = candidates.stream()
                .filter(RuleTarget::isFallback)
                .collect(Collectors.toList());

        // 先尝试主目标
        RuleTarget selected = selectByWeight(primary);
        if (selected != null) {
            return new SelectResult(selected, "primary selected by weight");
        }

        // 主目标全部不可用，降级到 fallback
        selected = selectByWeight(fallbacks);
        if (selected != null) {
            log.warn("All primary targets unavailable, falling back to {}", selected.getServiceId());
            return new SelectResult(selected, "fallback selected");
        }

        return new SelectResult(null, "all targets unavailable");
    }

    private RuleTarget selectByWeight(List<RuleTarget> targets) {
        if (targets == null || targets.isEmpty()) {
            return null;
        }

        // 过滤掉不健康的服务
        List<RuleTarget> healthy = targets.stream()
                .filter(this::isHealthy)
                .collect(Collectors.toList());

        if (healthy.isEmpty()) {
            return null;
        }

        // 如果只有一个候选，直接返回
        if (healthy.size() == 1) {
            return healthy.get(0);
        }

        // 加权随机选择
        return weightedRandom(healthy);
    }

    private boolean isHealthy(RuleTarget target) {
        try {
            List<ServiceCapabilityDO> caps = capabilityMapper.findByServiceId(target.getServiceId());
            if (caps.isEmpty()) {
                // 没有注册能力信息，假设健康
                return true;
            }
            return caps.stream().anyMatch(c -> Boolean.TRUE.equals(c.getHealthStatus()));
        } catch (Exception e) {
            log.debug("Health check failed for {}, assuming healthy: {}", target.getServiceId(), e.getMessage());
            return true;
        }
    }

    private RuleTarget weightedRandom(List<RuleTarget> targets) {
        int totalWeight = targets.stream().mapToInt(RuleTarget::getWeight).sum();
        if (totalWeight <= 0) {
            return targets.get(0);
        }

        int random = ThreadLocalRandom.current().nextInt(totalWeight);
        int cumulative = 0;
        for (RuleTarget target : targets) {
            cumulative += target.getWeight();
            if (random < cumulative) {
                return target;
            }
        }
        return targets.get(targets.size() - 1);
    }

    /**
     * 基于响应时间调整选择：优先选择响应快的服务。
     * 适用于有多候选且都有健康数据的场景。
     */
    public RuleTarget selectByResponseTime(List<RuleTarget> candidates) {
        return candidates.stream()
                .filter(this::isHealthy)
                .min(Comparator.comparingLong(this::getAvgResponseTime))
                .orElse(null);
    }

    private long getAvgResponseTime(RuleTarget target) {
        try {
            List<ServiceCapabilityDO> caps = capabilityMapper.findByServiceId(target.getServiceId());
            return caps.stream()
                    .filter(c -> c.getAvgResponseTimeMs() != null)
                    .mapToLong(ServiceCapabilityDO::getAvgResponseTimeMs)
                    .min()
                    .orElse(Long.MAX_VALUE);
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    public record SelectResult(RuleTarget selected, String reason) {}
}
