package com.ymware.engine.domain.rule.service;

import com.ymware.engine.domain.rule.model.ExecutionContext;
import com.ymware.engine.config.RuleEngineConfiguration;
import com.ymware.engine.exception.EngineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * Adapter allowing the rule-engine-open engine abstraction to execute
 * com.ymware.engine.core workflow (DAG flow) rules.
 */
@Slf4j
public class FlowRuleEngine extends Engine {

    private final RuleExecutor ruleExecutor;

    public FlowRuleEngine(@NonNull RuleEngineConfiguration configuration, RuleExecutor ruleExecutor) {
        super(configuration);
        this.ruleExecutor = ruleExecutor;
    }

    @Override
    public Output execute(@NonNull Input input, @NonNull String workspaceCode, @NonNull String code) {
        if (log.isDebugEnabled()) {
            log.debug("Executing flow rule: {} in workspace: {}", code, workspaceCode);
        }

        // Adapt Input parameter map to ExecutionContext
        ExecutionContext context = new ExecutionContext();
        Map<String, Object> params = input.getAll();
        if (params != null) {
            context.setVariables(params);
        }
        context.setVariable("workspaceCode", workspaceCode);
        context.setVariable("flowCode", code);

        // Run the workflow starting from the configured entryPoint
        ExecutionResult result;
        try {
            result = ruleExecutor.execute(context);
        } catch (com.ymware.engine.domain.rule.model.RuleExecutionException e) {
            throw new EngineException("Flow execution failed: " + e.getMessage(), e);
        }

        if (!result.isSuccess()) {
            throw new EngineException("Flow rule execution failed: " + result.getErrorMessage());
        }

        // Return the final variables from the execution context as the Output
        return new DefaultOutput(result.getContext().getAllVariables());
    }
}
