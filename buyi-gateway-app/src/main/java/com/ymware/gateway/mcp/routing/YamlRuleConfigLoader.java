package com.ymware.gateway.mcp.routing;

import com.ymware.gateway.mcp.routing.mapper.RoutingRuleMapper;
import com.ymware.gateway.mcp.routing.model.RoutingRuleDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 从 application.yml 加载路由规则配置，并同步到数据库。
 * 启动时自动导入 YAML 中定义的规则。
 */
@Component
@ConfigurationProperties(prefix = "gateway.mcp.routing")
public class YamlRuleConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(YamlRuleConfigLoader.class);

    private List<RuleConfig> rules = List.of();
    private final RoutingRuleMapper ruleMapper;
    private final ObjectMapper objectMapper;

    public YamlRuleConfigLoader(RoutingRuleMapper ruleMapper, ObjectMapper objectMapper) {
        this.ruleMapper = ruleMapper;
        this.objectMapper = objectMapper;
    }

    public List<RuleConfig> getRules() {
        return rules;
    }

    public void setRules(List<RuleConfig> rules) {
        this.rules = rules;
    }

    /**
     * 将 YAML 配置的规则同步到数据库（仅插入不存在的）。
     */
    public void syncToDatabase() {
        if (rules == null || rules.isEmpty()) {
            return;
        }

        for (RuleConfig config : rules) {
            try {
                // 检查是否已存在
                List<RoutingRuleDO> existing = ruleMapper.findByConditions(null, config.name, 0, 1);
                if (!existing.isEmpty()) {
                    log.debug("Rule '{}' already exists in database, skipping", config.name);
                    continue;
                }

                RoutingRuleDO record = new RoutingRuleDO();
                record.setRuleName(config.name);
                record.setDescription(config.description);
                record.setPriority(config.priority != null ? config.priority : 0);
                record.setMatchToolPattern(config.matchToolPattern);
                record.setMatchKeywords(config.matchKeywords);
                record.setMatchServiceType(config.matchServiceType);
                record.setMatchArgPath(config.matchArgPath);
                record.setTargetsJson(objectMapper.writeValueAsString(config.targets));
                record.setEnabled(config.enabled != null ? config.enabled : true);

                ruleMapper.insert(record);
                log.info("Imported routing rule '{}' from YAML config", config.name);
            } catch (Exception e) {
                log.error("Failed to import rule '{}': {}", config.name, e.getMessage());
            }
        }
    }

    public static class RuleConfig {
        private String name;
        private String description;
        private Integer priority;
        private String matchToolPattern;
        private String matchKeywords;
        private String matchServiceType;
        private String matchArgPath;
        private List<Map<String, Object>> targets;
        private Boolean enabled;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getPriority() { return priority; }
        public void setPriority(Integer priority) { this.priority = priority; }
        public String getMatchToolPattern() { return matchToolPattern; }
        public void setMatchToolPattern(String matchToolPattern) { this.matchToolPattern = matchToolPattern; }
        public String getMatchKeywords() { return matchKeywords; }
        public void setMatchKeywords(String matchKeywords) { this.matchKeywords = matchKeywords; }
        public String getMatchServiceType() { return matchServiceType; }
        public void setMatchServiceType(String matchServiceType) { this.matchServiceType = matchServiceType; }
        public String getMatchArgPath() { return matchArgPath; }
        public void setMatchArgPath(String matchArgPath) { this.matchArgPath = matchArgPath; }
        public List<Map<String, Object>> getTargets() { return targets; }
        public void setTargets(List<Map<String, Object>> targets) { this.targets = targets; }
        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    }
}
