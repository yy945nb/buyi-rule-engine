package com.ymware.engine.domain.rule.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ymware.engine.exception.EngineException;
import com.ymware.engine.entity.RuleEngineFlow;
import com.ymware.engine.cache.RuleFlowCache;
import com.ymware.engine.domain.rule.action.ActionRegistry;
import com.ymware.engine.domain.rule.model.ExecutionContext;
import com.ymware.engine.expression.ExpressionEvaluator;
import com.ymware.engine.mapper.RuleEngineFlowMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一规则引擎服务
 * 合并了原 RuleEngineService 和 RuleFlowService 的功能
 * 提供规则执行和流程管理的统一入口
 */
@Service
public class RuleEngineService {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineService.class);

    @Resource
    private RuleFlowCache ruleFlowCache;

    @Resource
    private RuleEngineFlowMapper ruleEngineFlowMapper;

    @Resource
    private ActionRegistry actionRegistry;

    @Resource
    private ExpressionEvaluator expressionEvaluator;

    /**
     * 默认的 RuleExecutor（由 Spring 自动配置注入）
     */
    private RuleExecutor defaultRuleExecutor;

    /**
     * 按 flowCode 缓存的 RuleExecutor
     */
    private final Map<String, RuleExecutor> executorCache = new ConcurrentHashMap<>();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 设置默认 RuleExecutor（由自动配置调用）
     */
    public void setDefaultRuleExecutor(RuleExecutor ruleExecutor) {
        this.defaultRuleExecutor = ruleExecutor;
    }

    // ==================== 规则执行方法 ====================

    /**
     * 使用执行上下文执行规则
     */
    public ExecutionResult execute(ExecutionContext context) {
        logger.debug("Executing rules with context");
        try {
            RuleExecutor executor = getDefaultExecutor();
            ExecutionResult result = executor.execute(context);
            if (result.isSuccess()) {
                logger.debug("Rule execution completed successfully in {}ms",
                        result.getExecutionTimeMs());
            } else {
                logger.warn("Rule execution failed: {}", result.getErrorMessage());
            }
            return result;
        } catch (Exception e) {
            logger.error("Unexpected error during rule execution", e);
            throw new RuleExecutionException("Rule execution failed", e);
        }
    }

    /**
     * 使用初始变量执行规则
     */
    public ExecutionResult execute(Map<String, Object> variables) {
        ExecutionContext context = new ExecutionContext();
        if (variables != null) {
            context.setVariables(variables);
        }
        return execute(context);
    }

    /**
     * 使用单个变量执行规则
     */
    public ExecutionResult execute(String key, Object value) {
        ExecutionContext context = new ExecutionContext();
        context.setVariable(key, value);
        return execute(context);
    }

    /**
     * 执行规则并返回指定的结果变量
     */
    public <T> T executeAndGet(ExecutionContext context, String variableName, Class<T> type) {
        ExecutionResult result = execute(context);
        if (result.isSuccess()) {
            return result.getContext().getVariable(variableName, type);
        }
        throw new RuleExecutionException(
                "Rule execution failed: " + result.getErrorMessage());
    }

    /**
     * 使用变量执行规则并返回指定结果
     */
    public <T> T executeAndGet(Map<String, Object> variables, String variableName, Class<T> type) {
        ExecutionContext context = new ExecutionContext();
        if (variables != null) {
            context.setVariables(variables);
        }
        return executeAndGet(context, variableName, type);
    }

    // ==================== 流程管理方法 ====================

    /**
     * 获取或构建指定流程的 RuleExecutor
     */
    public RuleExecutor getOrCreateExecutor(String flowCode) {
        FlowConfig config = ruleFlowCache.get(flowCode);
        if (config == null) {
            // 从数据库加载
            RuleEngineFlow flow = ruleEngineFlowMapper.selectOne(
                    new QueryWrapper<RuleEngineFlow>().eq("code", flowCode).eq("status", 1)
            );
            if (flow != null && flow.getConfigJson() != null) {
                try {
                    config = objectMapper.readValue(flow.getConfigJson(), FlowConfig.class);
                    ruleFlowCache.update(flowCode, config);
                } catch (Exception e) {
                    logger.error("Failed to parse flow config json for: {}", flowCode, e);
                    throw new EngineException("Invalid flow configuration: " + e.getMessage(), e);
                }
            }
        }

        if (config == null) {
            throw new IllegalArgumentException("Published rule flow not found: " + flowCode);
        }

        // 构建并缓存 executor
        return executorCache.computeIfAbsent(flowCode, k -> {
            try {
                return RuleEngineBuilder.create()
                        .withConfig(ruleFlowCache.get(flowCode))
                        .withActionRegistry(actionRegistry)
                        .withExpressionEvaluator(expressionEvaluator)
                        .withValidation(false)
                        .build();
            } catch (Exception e) {
                logger.error("Failed to build RuleExecutor for flow: {}", flowCode, e);
                throw new EngineException("Failed to compile flow: " + e.getMessage(), e);
            }
        });
    }

    /**
     * 按流程代码执行规则流程
     */
    public ExecutionResult executeFlow(String flowCode, Map<String, Object> variables) {
        RuleExecutor executor = getOrCreateExecutor(flowCode);
        ExecutionContext context = new ExecutionContext();
        if (variables != null) {
            context.setVariables(variables);
        }
        try {
            return executor.execute(context);
        } catch (com.ymware.engine.domain.rule.model.RuleExecutionException e) {
            throw new EngineException("Flow execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * 保存或更新草稿规则流程
     */
    public void saveFlow(RuleEngineFlow flow) {
        if (flow.getId() == null) {
            flow.setStatus(0); // 草稿
            flow.setCreateTime(new Date());
            flow.setUpdateTime(new Date());
            ruleEngineFlowMapper.insert(flow);
        } else {
            flow.setUpdateTime(new Date());
            ruleEngineFlowMapper.updateById(flow);
        }
        // 移除缓存
        executorCache.remove(flow.getCode());
        ruleFlowCache.delete(flow.getCode());
    }

    /**
     * 发布草稿规则流程
     */
    public void publishFlow(String flowCode) {
        RuleEngineFlow flow = ruleEngineFlowMapper.selectOne(
                new QueryWrapper<RuleEngineFlow>().eq("code", flowCode)
        );
        if (flow == null) {
            throw new IllegalArgumentException("Rule flow not found: " + flowCode);
        }

        // 发布前验证 JSON
        try {
            FlowConfig config = objectMapper.readValue(flow.getConfigJson(), FlowConfig.class);
            // 更新状态
            flow.setStatus(1); // 已发布
            flow.setUpdateTime(new Date());
            ruleEngineFlowMapper.updateById(flow);

            // 更新缓存
            ruleFlowCache.update(flowCode, config);
            executorCache.remove(flowCode); // 强制下次重建
            logger.info("Successfully published rule flow: {}", flowCode);
        } catch (Exception e) {
            logger.error("Validation failed during flow publishing", e);
            throw new EngineException("Failed to publish flow due to validation error: " + e.getMessage(), e);
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取默认的 RuleExecutor
     */
    private RuleExecutor getDefaultExecutor() {
        if (defaultRuleExecutor == null) {
            throw new IllegalStateException("No default RuleExecutor configured. Please configure a rule engine first.");
        }
        return defaultRuleExecutor;
    }

    /**
     * 获取底层 RuleExecutor
     */
    public RuleExecutor getRuleExecutor() {
        return getDefaultExecutor();
    }

    /**
     * 获取指定流程的 RuleExecutor
     */
    public RuleExecutor getRuleExecutor(String flowCode) {
        return getOrCreateExecutor(flowCode);
    }

    /**
     * 规则执行异常
     */
    public static class RuleExecutionException extends RuntimeException {
        public RuleExecutionException(String message) {
            super(message);
        }
        public RuleExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
