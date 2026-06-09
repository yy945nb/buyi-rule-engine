package com.ymware.gateway.mcp.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class McpServiceDO {
    private Long id;
    private String serviceId;
    private String name;
    private String description;
    private String endpoint;
    private String serviceType;   // TRANSPARENT, PROTOCOL_PARSE
    private String status;        // ACTIVE, INACTIVE, MAINTENANCE, DEPRECATED
    private Integer maxQps;
    private String healthCheckUrl;
    private String documentation;
    private String nacosServiceId;
    private Long versionNo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
}
