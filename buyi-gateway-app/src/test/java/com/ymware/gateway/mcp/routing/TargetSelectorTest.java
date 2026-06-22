package com.ymware.gateway.mcp.routing;

import com.ymware.gateway.mcp.routing.model.RuleTarget;
import com.ymware.gateway.mcp.routing.model.ServiceCapabilityDO;
import com.ymware.gateway.mcp.routing.mapper.ServiceCapabilityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * TargetSelector 测试 — 覆盖加权随机、健康过滤、fallback 降级
 */
@ExtendWith(MockitoExtension.class)
class TargetSelectorTest {

    @Mock
    private ServiceCapabilityMapper capabilityMapper;

    private TargetSelector selector;

    @BeforeEach
    void setUp() {
        selector = new TargetSelector(capabilityMapper);
    }

    @Test
    @DisplayName("null candidates → 返回 null")
    void select_nullCandidates() {
        TargetSelector.SelectResult result = selector.select(null);
        assertThat(result.selected()).isNull();
    }

    @Test
    @DisplayName("空 candidates → 返回 null")
    void select_emptyCandidates() {
        TargetSelector.SelectResult result = selector.select(Collections.emptyList());
        assertThat(result.selected()).isNull();
    }

    @Test
    @DisplayName("单候选 → 直接返回")
    void select_singleCandidate() {
        mockHealthy("svc-a");
        RuleTarget target = RuleTarget.builder().serviceId("svc-a").weight(100).build();

        TargetSelector.SelectResult result = selector.select(List.of(target));
        assertThat(result.selected()).isNotNull();
        assertThat(result.selected().getServiceId()).isEqualTo("svc-a");
    }

    @Test
    @DisplayName("主目标不健康时降级到 fallback")
    void select_primaryUnavailable_fallback() {
        mockUnhealthy("svc-primary");
        mockHealthy("svc-fallback");

        RuleTarget primary = RuleTarget.builder().serviceId("svc-primary").weight(100).fallback(false).build();
        RuleTarget fallback = RuleTarget.builder().serviceId("svc-fallback").weight(100).fallback(true).build();

        TargetSelector.SelectResult result = selector.select(List.of(primary, fallback));
        assertThat(result.selected()).isNotNull();
        assertThat(result.selected().getServiceId()).isEqualTo("svc-fallback");
    }

    @Test
    @DisplayName("全部不健康 → 返回 null")
    void select_allUnavailable() {
        mockUnhealthy("svc-a");
        mockUnhealthy("svc-b");

        RuleTarget a = RuleTarget.builder().serviceId("svc-a").weight(100).build();
        RuleTarget b = RuleTarget.builder().serviceId("svc-b").weight(100).build();

        TargetSelector.SelectResult result = selector.select(List.of(a, b));
        assertThat(result.selected()).isNull();
    }

    @Test
    @DisplayName("selectByResponseTime 选择响应最快的")
    void selectByResponseTime() {
        mockHealthyWithResponseTime("svc-fast", 50L);
        mockHealthyWithResponseTime("svc-slow", 500L);

        RuleTarget fast = RuleTarget.builder().serviceId("svc-fast").weight(100).build();
        RuleTarget slow = RuleTarget.builder().serviceId("svc-slow").weight(100).build();

        RuleTarget selected = selector.selectByResponseTime(List.of(fast, slow));
        assertThat(selected).isNotNull();
        assertThat(selected.getServiceId()).isEqualTo("svc-fast");
    }

    @Test
    @DisplayName("无健康数据的服务假设健康")
    void noCapabilityData_assumesHealthy() {
        when(capabilityMapper.findByServiceId("svc-unknown")).thenReturn(Collections.emptyList());

        RuleTarget target = RuleTarget.builder().serviceId("svc-unknown").weight(100).build();
        TargetSelector.SelectResult result = selector.select(List.of(target));
        assertThat(result.selected()).isNotNull();
    }

    // ===================== helpers =====================

    private void mockHealthy(String serviceId) {
        ServiceCapabilityDO cap = new ServiceCapabilityDO();
        cap.setServiceId(serviceId);
        cap.setHealthStatus(true);
        when(capabilityMapper.findByServiceId(serviceId)).thenReturn(List.of(cap));
    }

    private void mockUnhealthy(String serviceId) {
        ServiceCapabilityDO cap = new ServiceCapabilityDO();
        cap.setServiceId(serviceId);
        cap.setHealthStatus(false);
        when(capabilityMapper.findByServiceId(serviceId)).thenReturn(List.of(cap));
    }

    private void mockHealthyWithResponseTime(String serviceId, long responseTimeMs) {
        ServiceCapabilityDO cap = new ServiceCapabilityDO();
        cap.setServiceId(serviceId);
        cap.setHealthStatus(true);
        cap.setAvgResponseTimeMs(responseTimeMs);
        when(capabilityMapper.findByServiceId(serviceId)).thenReturn(List.of(cap));
    }
}
