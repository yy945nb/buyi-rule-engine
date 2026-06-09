package com.ymware.engine.domain.ai.gateway;

import java.util.Collections;
import java.util.List;

/**
 * Model router interface for selecting the best provider for a given request.
 * Inspired by AI-Gateway's ModelRouter with failover support.
 */
public interface LlmModelRouter {

    /**
     * Route a request to the best provider.
     *
     * @param model the model name or alias
     * @return the route result, or null if no provider found
     */
    LlmRouteResult route(String model);

    /**
     * Route a request to all available providers (ordered by priority for failover).
     *
     * @param model the model name or alias
     * @return ordered list of route results
     */
    List<LlmRouteResult> routeAll(String model);

    /**
     * Default implementation: single result wrapped in list.
     */
    default List<LlmRouteResult> routeAllDefault(String model) {
        LlmRouteResult result = route(model);
        if (result == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(result);
    }
}
