package com.ymware.gateway.mcp.routing;

import com.ymware.gateway.mcp.routing.model.RoutingRuleDO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 规则匹配器。
 * 判断一个 MCP 请求是否匹配某条路由规则。
 */
@Component
public class RuleMatcher {

    /**
     * 检查请求是否匹配规则的所有条件。
     *
     * @param rule            路由规则
     * @param toolName        工具名称（tools/call 时有值）
     * @param serviceType     当前请求的服务类型
     * @param toolArguments   工具调用参数
     * @param rawBody         原始请求体（可用于关键词匹配）
     * @return 匹配结果
     */
    public MatchResult match(RoutingRuleDO rule, String toolName, String serviceType,
                             JsonNode toolArguments, String rawBody) {
        // 1. 工具名模式匹配
        if (!matchToolPattern(rule.getMatchToolPattern(), toolName)) {
            return MatchResult.noMatch("tool pattern not matched");
        }

        // 2. 服务类型过滤
        if (!matchServiceType(rule.getMatchServiceType(), serviceType)) {
            return MatchResult.noMatch("service type not matched");
        }

        // 3. 意图关键词匹配
        if (!matchKeywords(rule.getMatchKeywords(), toolName, rawBody)) {
            return MatchResult.noMatch("keywords not matched");
        }

        // 4. 参数路径匹配
        if (!matchArgPath(rule.getMatchArgPath(), toolArguments)) {
            return MatchResult.noMatch("arg path not matched");
        }

        return MatchResult.matched(rule);
    }

    private boolean matchToolPattern(String pattern, String toolName) {
        if (pattern == null || pattern.isBlank()) {
            return true; // 无模式限制，匹配所有
        }
        if (toolName == null) {
            return false;
        }

        String[] patterns = pattern.split(",");
        for (String p : patterns) {
            if (matchWildcard(p.trim(), toolName)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchWildcard(String pattern, String value) {
        if (pattern.equals("*")) {
            return true;
        }
        // Convert wildcard pattern to regex
        String regex = pattern
                .replace(".", "\\.")
                .replace("*", ".*")
                .replace("?", ".");
        return value.matches(regex);
    }

    private boolean matchServiceType(String matchType, String serviceType) {
        if (matchType == null || matchType.isBlank() || "ALL".equalsIgnoreCase(matchType)) {
            return true;
        }
        if (serviceType == null) {
            return false;
        }
        return matchType.equalsIgnoreCase(serviceType);
    }

    private boolean matchKeywords(String keywords, String toolName, String rawBody) {
        if (keywords == null || keywords.isBlank()) {
            return true;
        }

        Set<String> keywordSet = Arrays.stream(keywords.split(","))
                .map(String::trim)
                .filter(k -> !k.isEmpty())
                .collect(Collectors.toSet());

        if (keywordSet.isEmpty()) {
            return true;
        }

        // 在工具名和原始请求体中搜索关键词
        String searchText = (toolName != null ? toolName : "") + " " + (rawBody != null ? rawBody : "");
        searchText = searchText.toLowerCase();

        for (String keyword : keywordSet) {
            if (searchText.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean matchArgPath(String argPath, JsonNode arguments) {
        if (argPath == null || argPath.isBlank()) {
            return true;
        }
        if (arguments == null) {
            return false;
        }

        // 格式: jsonPath=value，支持多个用逗号分隔
        String[] conditions = argPath.split(",");
        for (String condition : conditions) {
            String[] parts = condition.split("=", 2);
            if (parts.length != 2) continue;

            String path = parts[0].trim();
            String expectedValue = parts[1].trim();

            JsonNode node = resolvePath(arguments, path);
            if (node == null || !expectedValue.equals(node.asText())) {
                return false;
            }
        }
        return true;
    }

    private JsonNode resolvePath(JsonNode node, String path) {
        if (node == null || path == null) return null;
        String[] parts = path.split("\\.");
        JsonNode current = node;
        for (String part : parts) {
            if (current == null) return null;
            current = current.get(part);
        }
        return current;
    }

    public record MatchResult(boolean matched, RoutingRuleDO rule, String reason) {
        static MatchResult matched(RoutingRuleDO rule) {
            return new MatchResult(true, rule, null);
        }

        static MatchResult noMatch(String reason) {
            return new MatchResult(false, null, reason);
        }
    }
}
