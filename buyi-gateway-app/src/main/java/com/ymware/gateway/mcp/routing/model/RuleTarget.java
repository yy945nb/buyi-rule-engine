package com.ymware.gateway.mcp.routing.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 路由规则中的目标服务定义。
 * 一个规则可以有多个候选目标，按权重择优选择。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleTarget {

    private String serviceId;

    /** 权重，用于加权随机选择 */
    private int weight = 100;

    /** 能力标签，匹配时可按标签过滤 */
    private String capabilityTag;

    /** 备用目标标记：主目标不可用时才使用 */
    private boolean fallback;
}
