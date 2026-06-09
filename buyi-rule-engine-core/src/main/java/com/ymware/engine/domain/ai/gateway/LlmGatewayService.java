package com.ymware.engine.domain.ai.gateway;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI Gateway Service - the core orchestration layer.
 * Manages provider registration, model routing, and failover execution.
 *
 * Inspired by AI-Gateway's AbstractGatewayService + ChatGatewayService.
 *
 * Execution flow:
 * 1. Resolve model -> route to provider(s)
 * 2. Execute with primary provider
 * 3. On failure, failover to next candidate
 * 4. Collect stats and return result
 */
@Slf4j
public class LlmGatewayService {

    private final LlmProviderRegistry registry;

    /** Request statistics */
    private final Map<String, ProviderStats> statsMap = new ConcurrentHashMap<>();

    public LlmGatewayService(LlmProviderRegistry registry) {
        this.registry = registry;
    }

    /**
     * Send a chat request with automatic routing and failover.
     *
     * @param request the LLM request (model field is required)
     * @return the LLM response
     */
    public LlmResponse chat(LlmRequest request) {
        String model = request.getModel();
        if (model == null || model.isBlank()) {
            return LlmResponse.failure("Model is required", "gateway");
        }

        // Route to candidate providers
        List<LlmRouteResult> candidates = registry.routeAll(model);
        if (candidates.isEmpty()) {
            return LlmResponse.failure("No provider available for model: " + model, "gateway");
        }

        // Execute with failover
        LlmResponse lastResponse = null;
        List<String> attempted = new ArrayList<>();

        for (LlmRouteResult candidate : candidates) {
            LlmProvider provider = registry.getProvider(candidate.getProviderId());
            if (provider == null || !provider.isAvailable()) {
                log.debug("Skipping unavailable provider: {}", candidate.getProviderCode());
                continue;
            }

            // Build the actual request for this provider
            LlmRequest providerRequest = buildProviderRequest(request, candidate);
            attempted.add(candidate.getProviderCode());

            log.info("[Gateway] Routing model '{}' to {} (attempt {}/{})",
                    model, candidate.getProviderCode(), attempted.size(), candidates.size());

            lastResponse = provider.chat(providerRequest);

            // Record stats
            recordStats(candidate.getProviderCode(), lastResponse);

            if (lastResponse.isSuccess()) {
                if (attempted.size() > 1) {
                    log.info("[Gateway] Failover succeeded on provider {} after {} attempts",
                            candidate.getProviderCode(), attempted.size());
                }
                return lastResponse;
            }

            // Check if error is non-retryable (auth, bad request)
            if (isNonRetryableError(lastResponse)) {
                log.warn("[Gateway] Non-retryable error from {}: {}",
                        candidate.getProviderCode(), lastResponse.getErrorMessage());
                return lastResponse;
            }

            log.warn("[Gateway] Provider {} failed: {}, trying next candidate...",
                    candidate.getProviderCode(), lastResponse.getErrorMessage());
        }

        // All candidates exhausted
        log.error("[Gateway] All {} providers failed for model: {}", candidates.size(), model);
        return lastResponse != null ? lastResponse :
                LlmResponse.failure("All providers failed for model: " + model, "gateway");
    }

    /**
     * Chat with a specific provider (bypass routing).
     */
    public LlmResponse chatWithProvider(String providerId, LlmRequest request) {
        LlmProvider provider = registry.getProvider(providerId);
        if (provider == null) {
            return LlmResponse.failure("Provider not found: " + providerId, "gateway");
        }
        if (!provider.isAvailable()) {
            return LlmResponse.failure("Provider unavailable: " + providerId, "gateway");
        }

        LlmResponse response = provider.chat(request);
        recordStats(provider.getProviderCode(), response);
        return response;
    }

    /**
     * Get the provider registry.
     */
    public LlmProviderRegistry getRegistry() {
        return registry;
    }

    /**
     * Get stats for all providers.
     */
    public Map<String, ProviderStats> getStats() {
        return Map.copyOf(statsMap);
    }

    /**
     * Build a provider-specific request from the original request and route result.
     */
    private LlmRequest buildProviderRequest(LlmRequest original, LlmRouteResult route) {
        return LlmRequest.builder()
                .providerType(route.getProviderType().getCode())
                .model(route.getTargetModel())
                .messages(original.getMessages())
                .temperature(original.getTemperature())
                .maxTokens(original.getMaxTokens())
                .systemPrompt(original.getSystemPrompt())
                .prompt(original.getPrompt())
                .stream(original.isStream())
                .timeoutMs(original.getTimeoutMs())
                .retryCount(0) // No retry within provider when gateway handles failover
                .customHeaders(original.getCustomHeaders())
                .build();
    }

    /**
     * Check if error is non-retryable.
     */
    private boolean isNonRetryableError(LlmResponse response) {
        if (response.getErrorMessage() == null) return false;
        String msg = response.getErrorMessage().toLowerCase();
        return msg.contains("authentication failed") ||
               msg.contains("401") ||
               msg.contains("403") ||
               msg.contains("bad request") ||
               msg.contains("400");
    }

    /**
     * Record request statistics.
     */
    private void recordStats(String providerCode, LlmResponse response) {
        ProviderStats stats = statsMap.computeIfAbsent(providerCode, k -> new ProviderStats());
        stats.totalRequests++;
        if (response.isSuccess()) {
            stats.successRequests++;
            if (response.getUsage() != null) {
                stats.totalTokens += response.getUsage().getTotalTokens();
            }
            stats.totalLatencyMs += response.getLatencyMs();
        } else {
            stats.failedRequests++;
        }
    }

    /**
     * Provider statistics.
     */
    public static class ProviderStats {
        public long totalRequests;
        public long successRequests;
        public long failedRequests;
        public long totalTokens;
        public long totalLatencyMs;

        public double getSuccessRate() {
            return totalRequests == 0 ? 0 : (double) successRequests / totalRequests * 100;
        }

        public double getAvgLatencyMs() {
            return successRequests == 0 ? 0 : (double) totalLatencyMs / successRequests;
        }
    }
}
