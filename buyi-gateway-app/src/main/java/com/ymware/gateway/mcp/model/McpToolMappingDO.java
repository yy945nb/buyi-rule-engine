package com.ymware.gateway.mcp.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class McpToolMappingDO {
    private Long id;
    private String serviceId;
    private String toolName;
    private String toolDescription;
    private String inputSchemaJson;
    private String restEndpoint;
    private String restMethod;
    private String restHeadersJson;
    private String restParamMappingJson;
    private String responseMappingJson;
    private Boolean enabled;
    private Long versionNo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
}
