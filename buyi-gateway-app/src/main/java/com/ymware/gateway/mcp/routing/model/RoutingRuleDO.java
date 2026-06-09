package com.ymware.gateway.mcp.routing.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoutingRuleDO {
    private Long id;
    private String ruleName;
    private String description;
    private Integer priority;           // 越大越优先
    private String matchToolPattern;    // 工具名匹配模式，支持 * 通配符，逗号分隔多值
    private String matchKeywords;       // 意图关键词，逗号分隔
    private String matchServiceType;    // 服务类型过滤：TRANSPARENT / PROTOCOL_PARSE / ALL
    private String matchArgPath;        // 参数路径匹配，如 "category=文件"
    private String targetsJson;         // JSON: [{serviceId, weight, condition}]
    private Boolean enabled;
    private Long versionNo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
}
