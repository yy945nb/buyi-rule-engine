package com.ymware.gateway.mcp.routing;

import com.ymware.gateway.mcp.routing.mapper.RoutingRuleMapper;
import com.ymware.gateway.mcp.routing.model.RouteDecision;
import com.ymware.gateway.mcp.routing.model.RuleTarget;
import com.ymware.gateway.mcp.routing.model.RoutingRuleDO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * RoutingRuleEngine 测试 — 覆盖规则加载、匹配、目标解析
 */
@ExtendWith(MockitoExtension.class)
class RoutingRuleEngineTest {

    @Mock
    private RoutingRuleMapper ruleMapper;
    @Mock
    private RuleMatcher matcher;
    @Mock
    private TargetSelector selector;

    private RoutingRuleEngine engine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        engine = new RoutingRuleEngine(ruleMapper, matcher, selector, objectMapper);
    }

    @Nested
    @DisplayName("route 路由决策")
    class RouteTests {

        @Test
        @DisplayName("匹配到规则并返回目标")
        void route_matchingRule() {
            RoutingRuleDO rule = createRule("file-rule", 100,
                    "[{\"serviceId\":\"file-service\",\"weight\":100}]");
            when(ruleMapper.findAllEnabled()).thenReturn(List.of(rule));
            when(matcher.match(any(), anyString(), any(), any(), any()))
                    .thenReturn(RuleMatcher.MatchResult.matched(rule));
            when(selector.select(any())).thenReturn(
                    new TargetSelector.SelectResult(
                            RuleTarget.builder().serviceId("file-service").weight(100).build(),
                            "primary selected"));

            RouteDecision decision = engine.route("export_excel", "TRANSPARENT", null, null);

            assertThat(decision.getType()).isEqualTo(RouteDecision.DecisionType.RULE_MATCHED);
            assertThat(decision.getTargetServiceId()).isEqualTo("file-service");
        }

        @Test
        @DisplayName("无匹配规则 → NO_RULE_MATCH")
        void route_noMatch() {
            RoutingRuleDO rule = createRule("file-rule", 100, "[]");
            when(ruleMapper.findAllEnabled()).thenReturn(List.of(rule));
            when(matcher.match(any(), anyString(), any(), any(), any()))
                    .thenReturn(RuleMatcher.MatchResult.noMatch("not matched"));

            RouteDecision decision = engine.route("unknown_tool", "TRANSPARENT", null, null);

            assertThat(decision.getType()).isEqualTo(RouteDecision.DecisionType.NO_RULE_MATCH);
        }

        @Test
        @DisplayName("无规则配置 → NO_RULE_MATCH")
        void route_noRules() {
            when(ruleMapper.findAllEnabled()).thenReturn(Collections.emptyList());

            RouteDecision decision = engine.route("any_tool", "TRANSPARENT", null, null);

            assertThat(decision.getType()).isEqualTo(RouteDecision.DecisionType.NO_RULE_MATCH);
            assertThat(decision.getReason()).contains("no routing rules configured");
        }

        @Test
        @DisplayName("DB 查询异常 → NO_RULE_MATCH (容错)")
        void route_dbError() {
            when(ruleMapper.findAllEnabled()).thenThrow(new RuntimeException("DB error"));

            RouteDecision decision = engine.route("any_tool", "TRANSPARENT", null, null);

            assertThat(decision.getType()).isEqualTo(RouteDecision.DecisionType.NO_RULE_MATCH);
        }
    }

    @Nested
    @DisplayName("resolveTarget 目标解析")
    class ResolveTargetTests {

        @Test
        @DisplayName("规则 targetsJson 为空 → NO_RULE_MATCH")
        void resolveTarget_emptyTargets() {
            RoutingRuleDO rule = createRule("empty-rule", 100, null);
            when(ruleMapper.findAllEnabled()).thenReturn(List.of(rule));
            when(matcher.match(any(), anyString(), any(), any(), any()))
                    .thenReturn(RuleMatcher.MatchResult.matched(rule));

            RouteDecision decision = engine.route("tool", "TRANSPARENT", null, null);

            assertThat(decision.getType()).isEqualTo(RouteDecision.DecisionType.NO_RULE_MATCH);
            assertThat(decision.getReason()).contains("no targets");
        }

        @Test
        @DisplayName("targetsJson 非法 JSON → NO_RULE_MATCH (容错)")
        void resolveTarget_invalidJson() {
            RoutingRuleDO rule = createRule("bad-json-rule", 100, "not json");
            when(ruleMapper.findAllEnabled()).thenReturn(List.of(rule));
            when(matcher.match(any(), anyString(), any(), any(), any()))
                    .thenReturn(RuleMatcher.MatchResult.matched(rule));

            RouteDecision decision = engine.route("tool", "TRANSPARENT", null, null);

            assertThat(decision.getType()).isEqualTo(RouteDecision.DecisionType.NO_RULE_MATCH);
        }

        @Test
        @DisplayName("所有候选不可用 → FALLBACK_TO_DEFAULT")
        void resolveTarget_allUnavailable() {
            RoutingRuleDO rule = createRule("rule", 100,
                    "[{\"serviceId\":\"svc-a\",\"weight\":100}]");
            when(ruleMapper.findAllEnabled()).thenReturn(List.of(rule));
            when(matcher.match(any(), anyString(), any(), any(), any()))
                    .thenReturn(RuleMatcher.MatchResult.matched(rule));
            when(selector.select(any())).thenReturn(
                    new TargetSelector.SelectResult(null, "all targets unavailable"));

            RouteDecision decision = engine.route("tool", "TRANSPARENT", null, null);

            assertThat(decision.getType()).isEqualTo(RouteDecision.DecisionType.FALLBACK_TO_DEFAULT);
        }
    }

    // ===================== helpers =====================

    private RoutingRuleDO createRule(String name, int priority, String targetsJson) {
        RoutingRuleDO rule = new RoutingRuleDO();
        rule.setId(1L);
        rule.setRuleName(name);
        rule.setPriority(priority);
        rule.setTargetsJson(targetsJson);
        rule.setEnabled(true);
        return rule;
    }
}
