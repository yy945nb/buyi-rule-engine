package com.ymware.engine.domain.rule.action.builtin;

import com.ymware.engine.domain.ai.gateway.*;
import com.ymware.engine.domain.rule.action.Action;
import com.ymware.engine.domain.rule.action.ActionResult;
import com.ymware.engine.domain.rule.model.ActionException;
import com.ymware.engine.domain.rule.model.ExecutionContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * LLM Action - calls the AI Gateway to get LLM responses within rule execution.
 * Type: "LLM"
 *
 * Config keys:
 * - model: model name or alias (required)
 * - prompt: user prompt (required, supports ${variable} substitution)
 * - systemPrompt: system prompt (optional)
 * - temperature: sampling temperature (optional, default 0.7)
 * - maxTokens: max output tokens (optional)
 * - outputVariable: context variable to store the result (optional, default "llmResult")
 */
@Slf4j
public class LlmAction implements Action {

    private final String actionId;
    private final String model;
    private final String prompt;
    private final String systemPrompt;
    private final double temperature;
    private final Integer maxTokens;
    private final String outputVariable;
    private final LlmGatewayService gatewayService;

    public LlmAction(String actionId, Map<String, Object> config, LlmGatewayService gatewayService) {
        this.actionId = actionId;
        this.model = (String) config.getOrDefault("model", "");
        this.prompt = (String) config.getOrDefault("prompt", "");
        this.systemPrompt = (String) config.get("systemPrompt");
        this.temperature = config.containsKey("temperature") ?
                Double.parseDouble(config.get("temperature").toString()) : 0.7;
        this.maxTokens = config.containsKey("maxTokens") ?
                Integer.parseInt(config.get("maxTokens").toString()) : null;
        this.outputVariable = (String) config.getOrDefault("outputVariable", "llmResult");
        this.gatewayService = gatewayService;
    }

    @Override
    public ActionResult execute(ExecutionContext context) throws ActionException {
        try {
            // Substitute variables in prompt
            String resolvedPrompt = context.substituteVariables(prompt);
            String resolvedSystemPrompt = systemPrompt != null ? context.substituteVariables(systemPrompt) : null;
            String resolvedModel = context.substituteVariables(model);

            // Build request
            LlmRequest request = LlmRequest.builder()
                    .model(resolvedModel)
                    .prompt(resolvedPrompt)
                    .systemPrompt(resolvedSystemPrompt)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .build();

            log.debug("[LlmAction] Executing: model={}, promptLength={}", resolvedModel, resolvedPrompt.length());

            // Call gateway
            LlmResponse response = gatewayService.chat(request);

            if (response.isSuccess()) {
                // Store result in context
                context.setVariable(outputVariable, response.getContent());
                context.setVariable(outputVariable + "_usage", response.getUsage());
                context.setVariable(outputVariable + "_model", response.getModel());
                context.setVariable(outputVariable + "_provider", response.getProviderType());
                context.setVariable(outputVariable + "_latencyMs", response.getLatencyMs());

                log.debug("[LlmAction] Success: provider={}, latency={}ms, tokens={}",
                        response.getProviderType(), response.getLatencyMs(),
                        response.getUsage() != null ? response.getUsage().getTotalTokens() : "N/A");

                return ActionResult.success(response.getContent());
            } else {
                log.error("[LlmAction] Failed: {}", response.getErrorMessage());
                return ActionResult.failure("LLM call failed: " + response.getErrorMessage());
            }

        } catch (Exception e) {
            log.error("[LlmAction] Execution error", e);
            throw new ActionException("LLM action failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String getType() {
        return "LLM";
    }

    @Override
    public String getActionId() {
        return actionId;
    }

}
