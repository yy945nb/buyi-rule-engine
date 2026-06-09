package com.ymware.engine.domain.rule.action.builtin;

import com.ymware.engine.domain.ai.gateway.LlmGatewayService;
import com.ymware.engine.domain.rule.action.Action;
import com.ymware.engine.domain.rule.action.ActionProvider;
import com.ymware.engine.domain.rule.model.ActionCreationException;
import com.ymware.engine.domain.rule.service.ActionDefinition;

import java.util.Map;

/**
 * Action provider for LLM actions.
 * Type: "LLM"
 *
 * Creates LlmAction instances that call the AI Gateway.
 */
public class LlmActionProvider implements ActionProvider {

    private static final String ACTION_TYPE = "LLM";

    private final LlmGatewayService gatewayService;

    public LlmActionProvider(LlmGatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @Override
    public boolean supports(String actionType) {
        return ACTION_TYPE.equalsIgnoreCase(actionType);
    }

    @Override
    public Action createAction(ActionDefinition definition) throws ActionCreationException {
        Map<String, Object> config = definition.getConfig();
        if (config == null) {
            throw new ActionCreationException(ACTION_TYPE, definition.getActionId(),
                    "LLM action config is required");
        }

        // Validate required fields
        String model = (String) config.get("model");
        if (model == null || model.isBlank()) {
            throw new ActionCreationException(ACTION_TYPE, definition.getActionId(),
                    "LLM action requires 'model' in config");
        }

        String prompt = (String) config.get("prompt");
        if (prompt == null || prompt.isBlank()) {
            throw new ActionCreationException(ACTION_TYPE, definition.getActionId(),
                    "LLM action requires 'prompt' in config");
        }

        return new LlmAction(definition.getActionId(), config, gatewayService);
    }

    @Override
    public int getPriority() {
        return 100; // Framework-level priority
    }

    @Override
    public String getProviderName() {
        return "LlmActionProvider";
    }
}
