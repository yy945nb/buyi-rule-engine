package com.ymware.gateway.mcp.routing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路由决策结果。
 * 包含匹配到的规则、选定的目标服务、以及所有候选列表。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDecision {

    private DecisionType type;
    private String matchedRuleName;
    private String targetServiceId;
    private List<RuleTarget> candidates;
    private String reason;

    public enum DecisionType {
        /** 规则匹配成功，选定了目标 */
        RULE_MATCHED,
        /** 规则匹配但所有候选不可用，降级到默认路由 */
        FALLBACK_TO_DEFAULT,
        /** 无规则匹配，使用原始 serviceId 路由 */
        NO_RULE_MATCH,
        /** 拒绝：规则明确拒绝此请求 */
        DENIED
    }

    public static RouteDecision ruleMatched(String ruleName, String serviceId, List<RuleTarget> candidates) {
        return RouteDecision.builder()
                .type(DecisionType.RULE_MATCHED)
                .matchedRuleName(ruleName)
                .targetServiceId(serviceId)
                .candidates(candidates)
                .build();
    }

    public static RouteDecision noRuleMatch(String reason) {
        return RouteDecision.builder()
                .type(DecisionType.NO_RULE_MATCH)
                .reason(reason)
                .build();
    }

    public static RouteDecision fallback(String ruleName, String serviceId, String reason) {
        return RouteDecision.builder()
                .type(DecisionType.FALLBACK_TO_DEFAULT)
                .matchedRuleName(ruleName)
                .targetServiceId(serviceId)
                .reason(reason)
                .build();
    }

    public static RouteDecision denied(String reason) {
        return RouteDecision.builder()
                .type(DecisionType.DENIED)
                .reason(reason)
                .build();
    }
}
