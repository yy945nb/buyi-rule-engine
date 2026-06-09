package com.ymware.engine.domain.rule.action;
import com.ymware.engine.domain.rule.model.ActionException;

import com.ymware.engine.domain.rule.model.ExecutionContext;

/**
 * Core interface for all actions in the rule engine.
 * Actions are the atomic units of work that can be executed within a rule.
 */
public interface Action {

    /**
     * Execute the action with the given context.
     *
     * @param context The execution context containing variables and resources
     * @return ActionResult containing the execution outcome
     * @throws ActionException if the action execution fails
     */
    ActionResult execute(ExecutionContext context) throws ActionException;

    /**
     * Get the type of this action (e.g., "API", "DATABASE", "CACHE", "SCRIPT")
     *
     * @return The action type identifier
     */
    String getType();

    /**
     * Get the unique identifier of this action instance.
     *
     * @return The action ID from the rule definition
     */
    String getActionId();

}

