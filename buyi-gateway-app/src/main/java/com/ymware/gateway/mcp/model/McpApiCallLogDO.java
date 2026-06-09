package com.ymware.gateway.mcp.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class McpApiCallLogDO {
    private Long id;
    private String userId;
    private String serviceId;
    private Long authKeyId;
    private String requestPath;
    private String requestMethod;
    private String clientIp;
    private String userAgent;
    private Integer statusCode;
    private Integer responseTimeMs;
    private String errorMessage;
    private LocalDateTime createTime;
    private Boolean deleted;
}
