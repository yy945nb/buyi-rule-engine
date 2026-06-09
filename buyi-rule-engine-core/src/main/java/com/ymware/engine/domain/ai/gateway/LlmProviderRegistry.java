package com.ymware.engine.domain.ai.gateway;

import com.ymware.engine.domain.ai.gateway.provider.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry for LLM providers.
 * Manages provider instances and routes requests.
 * Inspired by AI-Gateway's ProviderClientFactory + PersistentModelRouter.
 */
@Slf4j
public class LlmProviderRegistry implements LlmModelRouter {

    /** Provider instances by ID */
    private final Map<String, LlmProvider> providers = new ConcurrentHashMap<>();

    /** Model alias mapping: alias -> providerId:modelName */
    private final Map<String, String> modelAliases = new ConcurrentHashMap<>();

    /**
     * Register a provider.
     */
    public void register(LlmProvider provider) {
        providers.put(provider.getProviderId(), provider);
        log.info("Registered LLM provider: {} ({})", provider.getDisplayName(), provider.getProviderCode());
    }

    /**
     * Register a provider from config.
     */
    public void register(LlmProviderConfig config) {
        LlmProvider provider = createProvider(config);
        register(provider);
    }

    /**
     * Unregister a provider.
     */
    public void unregister(String providerId) {
        LlmProvider removed = providers.remove(providerId);
        if (removed != null) {
            log.info("Unregistered LLM provider: {}", providerId);
        }
    }

    /**
     * Get a provider by ID.
     */
    public LlmProvider getProvider(String providerId) {
        return providers.get(providerId);
    }

    /**
     * Get all registered provider IDs.
     */
    public Set<String> getProviderIds() {
        return providers.keySet();
    }

    /**
     * Get all registered providers.
     */
    public List<LlmProvider> getAllProviders() {
        return new ArrayList<>(providers.values());
    }

    /**
     * Get all available (enabled and healthy) providers.
     */
    public List<LlmProvider> getAvailableProviders() {
        return providers.values().stream()
                .filter(LlmProvider::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * Get providers that support a specific model.
     */
    public List<LlmProvider> getProvidersForModel(String model) {
        return providers.values().stream()
                .filter(LlmProvider::isAvailable)
                .filter(p -> supportsModel(p, model))
                .collect(Collectors.toList());
    }

    /**
     * Register a model alias.
     * @param alias the alias name (e.g., "gpt-4")
     * @param target format: "providerId:modelName" (e.g., "openai-main:gpt-4-turbo")
     */
    public void registerAlias(String alias, String target) {
        modelAliases.put(alias.toLowerCase(), target);
        log.info("Registered model alias: {} -> {}", alias, target);
    }

    /**
     * Route a model request to the best provider.
     * Resolution order (inspired by AI-Gateway's PersistentModelRouter):
     * 1. Exact alias match
     * 2. Direct model name match
     * 3. Best available provider by priority
     */
    @Override
    public LlmRouteResult route(String model) {
        if (model == null || model.isBlank()) {
            return null;
        }

        String normalizedModel = model.trim().toLowerCase();

        // 1. Check alias mapping
        String aliasTarget = modelAliases.get(normalizedModel);
        if (aliasTarget != null) {
            return resolveAliasTarget(aliasTarget);
        }

        // 2. Find providers that support this model
        List<LlmProvider> candidates = getProvidersForModel(normalizedModel);
        if (!candidates.isEmpty()) {
            // Sort by priority (highest first)
            candidates.sort((a, b) -> Integer.compare(
                    b.getConfig().getPriority(),
                    a.getConfig().getPriority()
            ));
            LlmProvider best = candidates.get(0);
            return LlmRouteResult.builder()
                    .providerType(best.getProviderType())
                    .providerId(best.getProviderId())
                    .targetModel(normalizedModel)
                    .providerName(best.getDisplayName())
                    .priority(best.getConfig().getPriority())
                    .build();
        }

        // 3. Fallback to best available provider
        List<LlmProvider> available = getAvailableProviders();
        if (!available.isEmpty()) {
            available.sort((a, b) -> Integer.compare(
                    b.getConfig().getPriority(),
                    a.getConfig().getPriority()
            ));
            LlmProvider best = available.get(0);
            return LlmRouteResult.builder()
                    .providerType(best.getProviderType())
                    .providerId(best.getProviderId())
                    .targetModel(normalizedModel)
                    .providerName(best.getDisplayName())
                    .priority(best.getConfig().getPriority())
                    .build();
        }

        log.warn("No provider found for model: {}", model);
        return null;
    }

    /**
     * Route to all available providers for failover.
     */
    @Override
    public List<LlmRouteResult> routeAll(String model) {
        LlmRouteResult primary = route(model);
        if (primary == null) {
            return Collections.emptyList();
        }

        List<LlmRouteResult> results = new ArrayList<>();
        results.add(primary);

        // Add other available providers as fallback
        String normalizedModel = model != null ? model.trim().toLowerCase() : "";
        for (LlmProvider provider : getAvailableProviders()) {
            if (!provider.getProviderId().equals(primary.getProviderId())) {
                results.add(LlmRouteResult.builder()
                        .providerType(provider.getProviderType())
                        .providerId(provider.getProviderId())
                        .targetModel(normalizedModel)
                        .providerName(provider.getDisplayName())
                        .priority(provider.getConfig().getPriority() - 100) // Lower priority for fallback
                        .build());
            }
        }

        // Sort by priority
        results.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
        return results;
    }

    /**
     * Check if a provider supports a model.
     */
    private boolean supportsModel(LlmProvider provider, String model) {
        LlmProviderConfig config = provider.getConfig();
        if (config.getAvailableModels() == null || config.getAvailableModels().isEmpty()) {
            return true; // No model restriction
        }
        return config.getAvailableModels().stream()
                .anyMatch(m -> m.equalsIgnoreCase(model));
    }

    /**
     * Resolve alias target format: "providerId:modelName"
     */
    private LlmRouteResult resolveAliasTarget(String target) {
        String[] parts = target.split(":", 2);
        if (parts.length == 2) {
            String providerId = parts[0];
            String modelName = parts[1];
            LlmProvider provider = providers.get(providerId);
            if (provider != null && provider.isAvailable()) {
                return LlmRouteResult.builder()
                        .providerType(provider.getProviderType())
                        .providerId(providerId)
                        .targetModel(modelName)
                        .providerName(provider.getDisplayName())
                        .priority(provider.getConfig().getPriority())
                        .build();
            }
        }
        return null;
    }

    /**
     * Create a provider instance from config.
     */
    public static LlmProvider createProvider(LlmProviderConfig config) {
        return switch (config.getProviderType()) {
            case OPENAI -> new OpenAiProvider(config);
            case ZHIPU -> new ZhipuProvider(config);
            case DEEPSEEK -> new DeepSeekProvider(config);
            case QWEN -> new QwenProvider(config);
            case MIMO -> new MimoProvider(config);
            case MOONSHOT -> new MoonshotProvider(config);
            case OLLAMA -> new OllamaProvider(config);
            case CUSTOM -> new OpenAiProvider(config); // Custom uses OpenAI-compatible format
        };
    }
}
