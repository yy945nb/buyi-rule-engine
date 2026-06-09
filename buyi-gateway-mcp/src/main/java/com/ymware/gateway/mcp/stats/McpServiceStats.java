package com.ymware.gateway.mcp.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * MCP service statistics DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpServiceStats {

    private String serviceId;
    private long totalCalls;
    private long successCalls;
    private long failedCalls;
    private long avgResponseTimeMs;
    private long maxResponseTimeMs;
    private long uniqueUsers;
    private LocalDateTime lastCallTime;

    public double getSuccessRate() {
        if (totalCalls == 0) return 0.0;
        return (double) successCalls / totalCalls * 100;
    }
}
