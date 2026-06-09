package com.ymware.engine.domain.ai.gateway.provider;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ymware.engine.domain.ai.gateway.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Ollama provider for local LLM inference.
 * Uses Ollama's native API format with OpenAI-compatible fallback.
 * Default: http://localhost:11434
 */
@Slf4j
public class OllamaProvider extends AbstractLlmProvider {

    public OllamaProvider(LlmProviderConfig config) {
        super(config);
    }

    @Override
    public LlmProviderType getProviderType() {
        return LlmProviderType.OLLAMA;
    }

    @Override
    protected String buildChatUrl() {
        String host = config.getApiHost();
        if (!host.endsWith("/")) {
            host += "/";
        }
        // Ollama supports OpenAI-compatible endpoint since v0.1.14
        return host + "v1/chat/completions";
    }

    @Override
    protected HttpResponse executeRequest(String url, JSONObject body, int timeoutMs) {
        // Ollama doesn't require auth
        HttpRequest request = new HttpRequest(url)
                .method(Method.POST)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .timeout(timeoutMs);

        return request.execute();
    }

    @Override
    public boolean isAvailable() {
        if (!config.isEnabled()) {
            return false;
        }
        // Check if Ollama is running
        try {
            String host = config.getApiHost();
            if (!host.endsWith("/")) {
                host += "/";
            }
            HttpResponse response = HttpRequest.get(host + "api/tags")
                    .timeout(3000)
                    .execute();
            return response.isOk();
        } catch (Exception e) {
            log.warn("Ollama health check failed: {}", e.getMessage());
            return false;
        }
    }
}
