package com.ymware.gateway.mcp.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class McpServiceStatsDO {
    private Long id;
    private String serviceId;
    private LocalDate dateKey;
    private Integer totalCalls;
    private Integer successCalls;
    private Integer failedCalls;
    private Integer avgResponseTimeMs;
    private Integer maxResponseTimeMs;
    private Integer uniqueUsers;
    private Long versionNo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
}
