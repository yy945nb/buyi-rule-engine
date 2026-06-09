package com.ymware.gateway.mcp.routing;

import com.ymware.gateway.mcp.routing.mapper.RoutingRuleMapper;
import com.ymware.gateway.mcp.routing.model.RouteDecision;
import com.ymware.gateway.mcp.routing.model.RuleTarget;
import com.ymware.gateway.mcp.routing.model.RoutingRuleDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 路由规则引擎。
 * 接收 MCP 请求上下文，按优先级匹配规则，选择最佳目标服务。
 *
 * 决策流程：
 * 1. 加载所有启用规则（按优先级降序）
 * 2. 逐条匹配（工具名 → 服务类型 → 关键词 → 参数路径）
 * 3. 首条匹配规则生效
 * 4. 从匹配规则的候选目标中选择最佳服务
 * 5. 无规则匹配时，返回 NO_RULE_MATCH，由调用方走默认路由
 */
@Component
public class RoutingRuleEngine {

    private static final Logger log = LoggerFactory.getLogger(RoutingRuleEngine.class);

    private final RoutingRuleMapper ruleMapper;
    private final RuleMatcher matcher;
    private final TargetSelector selector;
    private final ObjectMapper objectMapper;

    public RoutingRuleEngine(RoutingRuleMapper ruleMapper, RuleMatcher matcher,
                             TargetSelector selector, ObjectMapper objectMapper) {
        this.ruleMapper = ruleMapper;
        this.matcher = matcher;
        this.selector = selector;
        this.objectMapper = objectMapper;
    }

    /**
     * 执行路由决策。
     *
     * @param toolName      工具名称
     * @param serviceType   服务类型（TRANSPARENT / PROTOCOL_PARSE）
     * @param toolArguments 工具调用参数
     * @param rawBody       原始请求体
     * @return 路由决策
     */
    public RouteDecision route(String toolName, String serviceType,
                               JsonNode toolArguments, String rawBody) {
        List<RoutingRuleDO> rules = loadEnabledRules();
        if (rules.isEmpty()) {
            return RouteDecision.noRuleMatch("no routing rules configured");
        }

        // 按优先级逐条匹配
        for (RoutingRuleDO rule : rules) {
            RuleMatcher.MatchResult matchResult = matcher.match(
                    rule, toolName, serviceType, toolArguments, rawBody);

            if (matchResult.matched()) {
                log.debug("Rule matched: {} for tool: {}", rule.getRuleName(), toolName);
                return resolveTarget(rule);
            }
        }

        return RouteDecision.noRuleMatch("no rule matched for tool: " + toolName);
    }

    /**
     * 简化版路由：只按工具名匹配。
     */
    public RouteDecision routeByToolName(String toolName) {
        return route(toolName, null, null, null);
    }

    private RouteDecision resolveTarget(RoutingRuleDO rule) {
        List<RuleTarget> targets = parseTargets(rule.getTargetsJson());
        if (targets.isEmpty()) {
            return RouteDecision.noRuleMatch("rule has no targets: " + rule.getRuleName());
        }

        TargetSelector.SelectResult selectResult = selector.select(targets);
        if (selectResult.selected() == null) {
            return RouteDecision.fallback(rule.getRuleName(), null, selectResult.reason());
        }

        return RouteDecision.ruleMatched(
                rule.getRuleName(),
                selectResult.selected().getServiceId(),
                targets
        );
    }

    private List<RuleTarget> parseTargets(String targetsJson) {
        if (targetsJson == null || targetsJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(targetsJson, new TypeReference<List<RuleTarget>>() {});
        } catch (Exception e) {
            log.error("Failed to parse targets JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<RoutingRuleDO> loadEnabledRules() {
        try {
            return ruleMapper.findAllEnabled();
        } catch (Exception e) {
            log.error("Failed to load routing rules: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
