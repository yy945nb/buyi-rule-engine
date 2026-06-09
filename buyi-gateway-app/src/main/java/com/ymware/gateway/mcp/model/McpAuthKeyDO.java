package com.ymware.gateway.mcp.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class McpAuthKeyDO {
    private Long id;
    private String keyHash;
    private String keyPrefix;
    private String userId;
    private String serviceId;
    private LocalDateTime expiresAt;
    private Boolean isActive;
    private Long versionNo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime lastUsedAt;
    private Boolean deleted;
}
