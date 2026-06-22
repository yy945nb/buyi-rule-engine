package com.ymware.gateway.mcp.routing;

import com.ymware.gateway.mcp.routing.model.RoutingRuleDO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RuleMatcher 测试 — 覆盖 4 种匹配器的分支逻辑
 */
class RuleMatcherTest {

    private RuleMatcher matcher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        matcher = new RuleMatcher();
    }

    // ===================== 工具名模式匹配 =====================

    @Nested
    @DisplayName("工具名模式匹配")
    class ToolPatternTests {

        @Test
        @DisplayName("通配符 export_* 匹配 export_excel")
        void wildcardMatch() {
            RoutingRuleDO rule = rule("export_*");
            assertThat(matcher.match(rule, "export_excel", "ALL", null, null).matched()).isTrue();
        }

        @Test
        @DisplayName("通配符 export_* 不匹配 import_csv")
        void wildcardNoMatch() {
            RoutingRuleDO rule = rule("export_*");
            assertThat(matcher.match(rule, "import_csv", "ALL", null, null).matched()).isFalse();
        }

        @Test
        @DisplayName("逗号分隔多模式: create_excel,create_pdf")
        void multiPattern() {
            RoutingRuleDO rule = rule("create_excel,create_pdf");
            assertThat(matcher.match(rule, "create_pdf", "ALL", null, null).matched()).isTrue();
            assertThat(matcher.match(rule, "create_word", "ALL", null, null).matched()).isFalse();
        }

        @Test
        @DisplayName("空模式匹配所有工具")
        void emptyPattern() {
            RoutingRuleDO rule = rule("");
            assertThat(matcher.match(rule, "anything", "ALL", null, null).matched()).isTrue();
        }

        @Test
        @DisplayName("null 模式匹配所有工具")
        void nullPattern() {
            RoutingRuleDO rule = rule(null);
            assertThat(matcher.match(rule, "anything", "ALL", null, null).matched()).isTrue();
        }

        @Test
        @DisplayName("有模式但 toolName 为 null → 不匹配")
        void patternWithNullToolName() {
            RoutingRuleDO rule = rule("export_*");
            assertThat(matcher.match(rule, null, "ALL", null, null).matched()).isFalse();
        }
    }

    // ===================== 服务类型过滤 =====================

    @Nested
    @DisplayName("服务类型过滤")
    class ServiceTypeTests {

        @Test
        @DisplayName("ALL 匹配所有服务类型")
        void matchAll() {
            RoutingRuleDO rule = ruleWithServiceType("ALL");
            assertThat(matcher.match(rule, "tool", "TRANSPARENT", null, null).matched()).isTrue();
            assertThat(matcher.match(rule, "tool", "PROTOCOL_PARSE", null, null).matched()).isTrue();
        }

        @Test
        @DisplayName("精确匹配 TRANSPARENT")
        void exactMatch() {
            RoutingRuleDO rule = ruleWithServiceType("TRANSPARENT");
            assertThat(matcher.match(rule, "tool", "TRANSPARENT", null, null).matched()).isTrue();
            assertThat(matcher.match(rule, "tool", "PROTOCOL_PARSE", null, null).matched()).isFalse();
        }

        @Test
        @DisplayName("大小写不敏感")
        void caseInsensitive() {
            RoutingRuleDO rule = ruleWithServiceType("transparent");
            assertThat(matcher.match(rule, "tool", "TRANSPARENT", null, null).matched()).isTrue();
        }

        @Test
        @DisplayName("null serviceType 不匹配非空规则")
        void nullServiceType() {
            RoutingRuleDO rule = ruleWithServiceType("TRANSPARENT");
            assertThat(matcher.match(rule, "tool", null, null, null).matched()).isFalse();
        }
    }

    // ===================== 关键词匹配 =====================

    @Nested
    @DisplayName("关键词匹配")
    class KeywordTests {

        @Test
        @DisplayName("命中工具名")
        void hitToolName() {
            RoutingRuleDO rule = ruleWithKeywords("导出,报表");
            assertThat(matcher.match(rule, "导出_excel", "ALL", null, null).matched()).isTrue();
        }

        @Test
        @DisplayName("命中 rawBody")
        void hitRawBody() {
            RoutingRuleDO rule = ruleWithKeywords("导出,报表");
            assertThat(matcher.match(rule, "tool", "ALL", null, "请帮我生成一份报表").matched()).isTrue();
        }

        @Test
        @DisplayName("无关键词匹配所有")
        void noKeywords() {
            RoutingRuleDO rule = ruleWithKeywords("");
            assertThat(matcher.match(rule, "tool", "ALL", null, null).matched()).isTrue();
        }

        @Test
        @DisplayName("null 关键词匹配所有")
        void nullKeywords() {
            RoutingRuleDO rule = ruleWithKeywords(null);
            assertThat(matcher.match(rule, "tool", "ALL", null, null).matched()).isTrue();
        }
    }

    // ===================== 参数路径匹配 =====================

    @Nested
    @DisplayName("参数路径匹配")
    class ArgPathTests {

        @Test
        @DisplayName("单路径匹配")
        void singlePath() {
            RoutingRuleDO rule = ruleWithArgPath("category=文件");
            ObjectNode args = objectMapper.createObjectNode().put("category", "文件");
            assertThat(matcher.match(rule, "tool", "ALL", args, null).matched()).isTrue();
        }

        @Test
        @DisplayName("路径值不匹配")
        void pathValueMismatch() {
            RoutingRuleDO rule = ruleWithArgPath("category=文件");
            ObjectNode args = objectMapper.createObjectNode().put("category", "图片");
            assertThat(matcher.match(rule, "tool", "ALL", args, null).matched()).isFalse();
        }

        @Test
        @DisplayName("多路径 AND 逻辑")
        void multiPathAnd() {
            RoutingRuleDO rule = ruleWithArgPath("category=文件,action=导出");
            ObjectNode args = objectMapper.createObjectNode()
                    .put("category", "文件").put("action", "导出");
            assertThat(matcher.match(rule, "tool", "ALL", args, null).matched()).isTrue();

            ObjectNode args2 = objectMapper.createObjectNode()
                    .put("category", "文件").put("action", "导入");
            assertThat(matcher.match(rule, "tool", "ALL", args2, null).matched()).isFalse();
        }

        @Test
        @DisplayName("null arguments 不匹配非空规则")
        void nullArguments() {
            RoutingRuleDO rule = ruleWithArgPath("category=文件");
            assertThat(matcher.match(rule, "tool", "ALL", null, null).matched()).isFalse();
        }

        @Test
        @DisplayName("路径不存在不匹配")
        void pathNotFound() {
            RoutingRuleDO rule = ruleWithArgPath("category=文件");
            ObjectNode args = objectMapper.createObjectNode().put("type", "pdf");
            assertThat(matcher.match(rule, "tool", "ALL", args, null).matched()).isFalse();
        }
    }

    // ===================== 组合测试 =====================

    @Nested
    @DisplayName("组合条件")
    class CombinedTests {

        @Test
        @DisplayName("全部条件满足 → matched")
        void allMatch() {
            RoutingRuleDO rule = new RoutingRuleDO();
            rule.setMatchToolPattern("export_*");
            rule.setMatchServiceType("TRANSPARENT");
            rule.setMatchKeywords("导出");
            ObjectNode args = objectMapper.createObjectNode().put("format", "xlsx");
            rule.setMatchArgPath("format=xlsx");

            RuleMatcher.MatchResult result = matcher.match(rule, "export_excel", "TRANSPARENT", args, "导出数据");
            assertThat(result.matched()).isTrue();
        }

        @Test
        @DisplayName("任一条件不满足 → noMatch")
        void oneFail() {
            RoutingRuleDO rule = new RoutingRuleDO();
            rule.setMatchToolPattern("export_*");
            rule.setMatchServiceType("TRANSPARENT");
            rule.setMatchKeywords("导出");
            rule.setMatchArgPath("format=xlsx");

            // serviceType 不匹配
            RuleMatcher.MatchResult result = matcher.match(rule, "export_excel", "PROTOCOL_PARSE", null, "导出数据");
            assertThat(result.matched()).isFalse();
        }
    }

    // ===================== helpers =====================

    private RoutingRuleDO rule(String toolPattern) {
        RoutingRuleDO r = new RoutingRuleDO();
        r.setMatchToolPattern(toolPattern);
        return r;
    }

    private RoutingRuleDO ruleWithServiceType(String serviceType) {
        RoutingRuleDO r = new RoutingRuleDO();
        r.setMatchServiceType(serviceType);
        return r;
    }

    private RoutingRuleDO ruleWithKeywords(String keywords) {
        RoutingRuleDO r = new RoutingRuleDO();
        r.setMatchKeywords(keywords);
        return r;
    }

    private RoutingRuleDO ruleWithArgPath(String argPath) {
        RoutingRuleDO r = new RoutingRuleDO();
        r.setMatchArgPath(argPath);
        return r;
    }
}
