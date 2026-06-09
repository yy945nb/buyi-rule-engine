package com.ymware.gateway.mcp.routing.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ServiceCapabilityDO {
    private Long id;
    private String serviceId;
    private String capabilityTag;       // 能力标签：file, image, database, order, product...
    private String description;
    private Integer maxConcurrent;      // 最大并发
    private Integer weight;             // 默认权重
    private Boolean healthStatus;       // 当前健康状态
    private Long avgResponseTimeMs;     // 平均响应时间（运行时更新）
    private Long versionNo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
}
