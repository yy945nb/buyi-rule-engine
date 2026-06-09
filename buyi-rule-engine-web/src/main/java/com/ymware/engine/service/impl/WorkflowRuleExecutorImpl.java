package com.ymware.engine.service.impl;

import com.ymware.engine.domain.workflow.service.WorkflowRuleExecutor;
import com.ymware.engine.domain.rule.service.DefaultInput;
import com.ymware.engine.domain.rule.service.GeneralRuleEngine;
import com.ymware.engine.domain.rule.service.Input;
import com.ymware.engine.domain.rule.service.Output;
import com.ymware.engine.domain.rule.service.RuleEngineService;
import com.ymware.engine.domain.rule.service.ExecutionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WorkflowRuleExecutorImpl implements WorkflowRuleExecutor {

    @Autowired
    @Lazy
    private RuleEngineService ruleEngineService;

    @Autowired
    @Lazy
    private GeneralRuleEngine generalRuleEngine;

    @Override
    public Map<String, Object> execute(String code, String workspaceCode, Map<String, Object> variables) {
        log.info("WorkflowRuleExecutor execution started for code: {}, workspaceCode: {}", code, workspaceCode);

        // 1. Try rule flow execution first
        try {
            log.info("Trying rule flow execution for code: {}", code);
            ExecutionResult executionResult = ruleEngineService.executeFlow(code, variables);
            if (executionResult != null && executionResult.isSuccess()) {
                log.info("Rule flow execution succeeded for code: {}", code);
                return executionResult.getContext().getAllVariables();
            } else if (executionResult != null) {
                log.error("Rule flow execution failed: {}", executionResult.getErrorMessage());
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("error", executionResult.getErrorMessage());
                return errorResult;
            }
        } catch (IllegalArgumentException e) {
            log.info("Rule flow not found for code: {}, falling back to general rule execution. Message: {}", code, e.getMessage());
        } catch (Exception e) {
            log.error("Exception during rule flow execution: {}", e.getMessage(), e);
        }

        // 2. Fallback to general rule execution
        try {
            log.info("Executing general rule for code: {}, workspaceCode: {}", code, workspaceCode);
            Input input = new DefaultInput(variables != null ? variables : new HashMap<>());
            Output output = generalRuleEngine.execute(input, workspaceCode, code);
            Map<String, Object> result = new HashMap<>();
            if (output != null) {
                Object value = output.getValue();
                if (value instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> valueMap = (Map<String, Object>) value;
                    result.putAll(valueMap);
                } else {
                    result.put("result", value);
                }
            }
            result.put("success", true);
            return result;
        } catch (Exception e) {
            log.error("General rule execution failed for code: {}", code, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", e.getMessage());
            return errorResult;
        }
    }
}
