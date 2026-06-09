package com.ymware.engine.cache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ymware.engine.entity.RuleEngineFlow;
import com.ymware.engine.domain.rule.service.FlowConfig;
import com.ymware.engine.mapper.RuleEngineFlowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * Cache for active RuleEngine DAG Flow configurations.
 */
@Slf4j
@Component
public class RuleFlowCache extends AbstractCache<FlowConfig> {

    @Resource
    private RuleEngineFlowMapper ruleEngineFlowMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public synchronized void update(String code, FlowConfig value) {
        cacheMap.put(code, value);
    }

    @Override
    public synchronized void delete(String code) {
        if (StringUtils.isEmpty(code)) {
            return;
        }
        cacheMap.remove(code);
    }

    @Override
    public void init() {
        log.info("Loading published rule engine flows from database...");
        try {
            List<RuleEngineFlow> flows = ruleEngineFlowMapper.selectList(
                    new QueryWrapper<RuleEngineFlow>().eq("status", 1) // status 1 = published
            );
            if (flows != null) {
                log.info("Found {} published rule engine flows to load", flows.size());
                for (RuleEngineFlow flow : flows) {
                    try {
                        if (flow.getConfigJson() != null && !flow.getConfigJson().trim().isEmpty()) {
                            FlowConfig config = objectMapper.readValue(flow.getConfigJson(), FlowConfig.class);
                            this.update(flow.getCode(), config);
                            log.info("Successfully loaded rule engine flow: {}", flow.getCode());
                        }
                    } catch (Exception e) {
                        log.error("Failed to parse config JSON for rule engine flow: {}", flow.getCode(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to load rule engine flows from database", e);
        }
    }
}
