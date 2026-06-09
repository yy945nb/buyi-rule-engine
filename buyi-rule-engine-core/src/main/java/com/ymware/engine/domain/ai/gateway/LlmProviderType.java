package com.ymware.engine.domain.ai.gateway;

import java.util.Arrays;
import java.util.Set;

/**
 * LLM provider types supported by the AI Gateway.
 * Inspired by AI-Gateway's ProviderType with alias support.
 */
public enum LlmProviderType {

    OPENAI("openai", "OpenAI", true, Set.of("gpt", "chatgpt")),
    ZHIPU("zhipu", "Zhipu GLM", true, Set.of("glm", "chatglm")),
    DEEPSEEK("deepseek", "DeepSeek", true, Set.of("deep-seek")),
    QWEN("qwen", "Alibaba Qwen", true, Set.of("tongyi", "dashscope")),
    MIMO("mimo", "Xiaomi Mimo", true, Set.of("xiaomi")),
    MOONSHOT("moonshot", "Moonshot AI", true, Set.of("kimi")),
    OLLAMA("ollama", "Ollama (Local)", false, Set.of("local")),
    CUSTOM("custom", "Custom OpenAI-Compatible", true, Set.of());

    private final String code;
    private final String displayName;
    private final boolean cloudProvider;
    private final Set<String> aliases;

    LlmProviderType(String code, String displayName, boolean cloudProvider, Set<String> aliases) {
        this.code = code;
        this.displayName = displayName;
        this.cloudProvider = cloudProvider;
        this.aliases = aliases;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCloudProvider() {
        return cloudProvider;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    /**
     * Resolve provider type from code or alias.
     */
    public static LlmProviderType fromCode(String code) {
        if (code == null || code.isBlank()) {
            return CUSTOM;
        }
        String normalized = code.trim().toLowerCase();
        for (LlmProviderType type : values()) {
            if (type.code.equals(normalized) || type.aliases.contains(normalized)) {
                return type;
            }
        }
        return CUSTOM;
    }

    /**
     * Check if the given code is a known provider.
     */
    public static boolean isKnownProvider(String code) {
        return fromCode(code) != CUSTOM;
    }

    /**
     * Get default API host for known providers.
     */
    public String getDefaultApiHost() {
        return switch (this) {
            case OPENAI -> "https://api.openai.com/v1/";
            case ZHIPU -> "https://open.bigmodel.cn/api/paas/v4/";
            case DEEPSEEK -> "https://api.deepseek.com/v1/";
            case QWEN -> "https://dashscope.aliyuncs.com/compatible-mode/v1/";
            case MIMO -> "https://api.mimo.ai/v1/";
            case MOONSHOT -> "https://api.moonshot.cn/v1/";
            case OLLAMA -> "http://localhost:11434/";
            case CUSTOM -> null;
        };
    }
}
