package com.ymware.gateway.mcp.discovery;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * McpServiceInfo 测试
 */
class McpServiceInfoTest {

    @Test
    @DisplayName("ACTIVE 状态返回 true")
    void isActive_active() {
        McpServiceInfo info = McpServiceInfo.builder()
                .status(McpServiceInfo.ServiceStatus.ACTIVE).build();
        assertThat(info.isActive()).isTrue();
    }

    @Test
    @DisplayName("INACTIVE 状态返回 false")
    void isActive_inactive() {
        McpServiceInfo info = McpServiceInfo.builder()
                .status(McpServiceInfo.ServiceStatus.INACTIVE).build();
        assertThat(info.isActive()).isFalse();
    }

    @Test
    @DisplayName("MAINTENANCE 状态返回 false")
    void isActive_maintenance() {
        McpServiceInfo info = McpServiceInfo.builder()
                .status(McpServiceInfo.ServiceStatus.MAINTENANCE).build();
        assertThat(info.isActive()).isFalse();
    }

    @Test
    @DisplayName("DEPRECATED 状态返回 false")
    void isActive_deprecated() {
        McpServiceInfo info = McpServiceInfo.builder()
                .status(McpServiceInfo.ServiceStatus.DEPRECATED).build();
        assertThat(info.isActive()).isFalse();
    }

    @Test
    @DisplayName("null 状态返回 false")
    void isActive_nullStatus() {
        McpServiceInfo info = McpServiceInfo.builder().build();
        assertThat(info.isActive()).isFalse();
    }
}
