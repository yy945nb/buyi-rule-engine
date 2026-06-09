package com.ymware.gateway.mcp.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * MCP auth key info DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpAuthKeyInfo {

    private Long id;
    private String keyHash;
    private String keyPrefix;
    private String userId;
    private String serviceId;
    private LocalDateTime expiresAt;
    private boolean isActive;
    private LocalDateTime lastUsedAt;

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return isActive && !isExpired();
    }
}
