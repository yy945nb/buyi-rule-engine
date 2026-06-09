package com.ymware.gateway.mcp.discovery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP service information DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpServiceInfo {

    private String serviceId;
    private String name;
    private String description;
    private String endpoint;
    private ServiceType serviceType;
    private ServiceStatus status;
    private int maxQps;
    private String healthCheckUrl;
    private String nacosServiceId;

    public enum ServiceType {
        TRANSPARENT, PROTOCOL_PARSE
    }

    public enum ServiceStatus {
        ACTIVE, INACTIVE, MAINTENANCE, DEPRECATED
    }

    public boolean isActive() {
        return status == ServiceStatus.ACTIVE;
    }
}
