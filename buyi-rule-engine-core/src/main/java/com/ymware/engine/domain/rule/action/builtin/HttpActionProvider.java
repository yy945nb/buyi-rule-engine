package com.ymware.engine.domain.rule.action.builtin;

import com.ymware.engine.domain.rule.action.Action;
import com.ymware.engine.domain.rule.action.ActionProvider;
import com.ymware.engine.domain.rule.model.ActionCreationException;
import com.ymware.engine.domain.rule.service.ActionDefinition;

import java.util.Map;

/**
 * Action provider for HTTP actions.
 * Type: "HTTP"
 */
public class HttpActionProvider implements ActionProvider {

    private static final String ACTION_TYPE = "HTTP";

    @Override
    public boolean supports(String actionType) {
        return ACTION_TYPE.equalsIgnoreCase(actionType);
    }

    @Override
    public Action createAction(ActionDefinition definition) throws ActionCreationException {
        Map<String, Object> config = definition.getConfig();
        if (config == null) {
            throw new ActionCreationException(ACTION_TYPE, definition.getActionId(),
                    "HTTP action config is required");
        }

        String url = (String) config.get("url");
        if (url == null || url.isBlank()) {
            throw new ActionCreationException(ACTION_TYPE, definition.getActionId(),
                    "HTTP action requires 'url' in config");
        }

        return new HttpAction(definition.getActionId(), config);
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public String getProviderName() {
        return "HttpActionProvider";
    }
}
