package com.ymware.engine.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import com.ymware.engine.cache.DataSourceConfigCache;
import com.ymware.engine.cache.RuleExecuteContextCache;
import com.ymware.engine.cache.RuleFlowCache;
/**
 * Initializes dynamic data sources, tag rules, and DAG flows from database on startup.
 */
@Component
@Slf4j
public class ChannelEngineInitializer implements ApplicationRunner {

    @Resource
    private DataSourceConfigCache dataSourceConfigCache;

    @Resource
    private RuleExecuteContextCache ruleExecuteContextCache;

    @Resource
    private RuleFlowCache ruleFlowCache;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Initializing Channel Rule Engine components...");
        try {
            dataSourceConfigCache.init();
            log.info("DataSourceConfigCache initialized successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize DataSourceConfigCache", e);
        }

        try {
            ruleExecuteContextCache.init();
            log.info("RuleExecuteContextCache initialized successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize RuleExecuteContextCache", e);
        }

        try {
            ruleFlowCache.init();
            log.info("RuleFlowCache initialized successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize RuleFlowCache", e);
        }
    }
}
