package com.ymware.gateway.mcp.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * McpAuthKeyInfo 测试
 */
class McpAuthKeyInfoTest {

    @Test
    @DisplayName("expiresAt 为 null 时不过期")
    void isExpired_nullExpiresAt() {
        McpAuthKeyInfo info = McpAuthKeyInfo.builder().expiresAt(null).isActive(true).build();
        assertThat(info.isExpired()).isFalse();
    }

    @Test
    @DisplayName("过去日期返回 true")
    void isExpired_pastDate() {
        McpAuthKeyInfo info = McpAuthKeyInfo.builder()
                .expiresAt(LocalDateTime.now().minusDays(1)).isActive(true).build();
        assertThat(info.isExpired()).isTrue();
    }

    @Test
    @DisplayName("未来日期返回 false")
    void isExpired_futureDate() {
        McpAuthKeyInfo info = McpAuthKeyInfo.builder()
                .expiresAt(LocalDateTime.now().plusDays(1)).isActive(true).build();
        assertThat(info.isExpired()).isFalse();
    }

    @Test
    @DisplayName("active + 未过期 → valid")
    void isValid_activeAndNotExpired() {
        McpAuthKeyInfo info = McpAuthKeyInfo.builder()
                .isActive(true).expiresAt(LocalDateTime.now().plusDays(1)).build();
        assertThat(info.isValid()).isTrue();
    }

    @Test
    @DisplayName("inactive + 未过期 → invalid")
    void isValid_inactiveButNotExpired() {
        McpAuthKeyInfo info = McpAuthKeyInfo.builder()
                .isActive(false).expiresAt(LocalDateTime.now().plusDays(1)).build();
        assertThat(info.isValid()).isFalse();
    }

    @Test
    @DisplayName("active + 已过期 → invalid")
    void isValid_activeButExpired() {
        McpAuthKeyInfo info = McpAuthKeyInfo.builder()
                .isActive(true).expiresAt(LocalDateTime.now().minusDays(1)).build();
        assertThat(info.isValid()).isFalse();
    }

    @Test
    @DisplayName("active + expiresAt 为 null → valid (永不过期)")
    void isValid_activeAndNullExpiresAt() {
        McpAuthKeyInfo info = McpAuthKeyInfo.builder()
                .isActive(true).expiresAt(null).build();
        assertThat(info.isValid()).isTrue();
    }
}
