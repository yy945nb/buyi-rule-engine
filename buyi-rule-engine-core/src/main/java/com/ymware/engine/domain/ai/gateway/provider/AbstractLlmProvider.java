package com.ymware.engine.domain.ai.gateway.provider;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ymware.engine.domain.ai.gateway.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base for LLM providers using OpenAI-compatible chat completions API.
 * Inspired by AI-Gateway's AbstractProviderClient with retry and error mapping.
 *
 * Covers: OpenAI, DeepSeek, Qwen, Zhipu GLM, Mimo, Moonshot, Ollama.
 */
@Slf4j
public abstract class AbstractLlmProvider implements LlmProvider {

    protected final LlmProviderConfig config;

    protected AbstractLlmProvider(LlmProviderConfig config) {
        this.config = config;
    }

    @Override
    public LlmProviderConfig getConfig() {
        return config;
    }

    @Override
    public String getProviderId() {
        return config.getId();
    }

    @Override
    public boolean isAvailable() {
        return config.isEnabled();
    }

    @Override
    public LlmResponse chat(LlmRequest request) {
        long startTime = System.currentTimeMillis();
        String providerCode = getProviderCode();
        int maxRetries = request.getRetryCount() > 0 ? request.getRetryCount() : config.getMaxRetries();

        Exception lastException = null;
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                if (attempt > 0) {
                    long backoff = (long) Math.min(1000 * Math.pow(2, attempt - 1), 10000);
                    Thread.sleep(backoff);
                    log.info("[{}] Retry attempt {}/{} after {}ms", providerCode, attempt, maxRetries, backoff);
                }

                LlmResponse response = doChat(request);
                response.setLatencyMs(System.currentTimeMillis() - startTime);
                response.setProviderType(getProviderType().getCode());

                if (response.isSuccess()) {
                    return response;
                }

                // Non-retryable errors
                if (isNonRetryableError(response)) {
                    return response;
                }

                lastException = new RuntimeException(response.getErrorMessage());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return LlmResponse.failure("Request interrupted", getProviderType().getCode());
            } catch (Exception e) {
                lastException = e;
                log.warn("[{}] Request attempt {} failed: {}", providerCode, attempt, e.getMessage());
            }
        }

        log.error("[{}] All {} attempts failed", providerCode, maxRetries + 1, lastException);
        return LlmResponse.failure(
                "All retries exhausted: " + (lastException != null ? lastException.getMessage() : "unknown"),
                getProviderType().getCode()
        );
    }

    /**
     * Execute a single chat request. Subclasses can override for provider-specific behavior.
     */
    protected LlmResponse doChat(LlmRequest request) {
        try {
            List<LlmMessage> messages = buildMessages(request);
            JSONObject body = buildRequestBody(request, messages);
            String url = buildChatUrl();
            HttpResponse httpResponse = executeRequest(url, body, request.getTimeoutMs());

            if (!httpResponse.isOk()) {
                String errorBody = httpResponse.body();
                int status = httpResponse.getStatus();
                log.error("[{}] API error: status={}, body={}", getProviderCode(), status, errorBody);
                return LlmResponse.failure(
                        "HTTP " + status + ": " + mapErrorMessage(status, errorBody),
                        getProviderType().getCode()
                );
            }

            return parseResponse(httpResponse.body());

        } catch (Exception e) {
            log.error("[{}] Request failed", getProviderCode(), e);
            return LlmResponse.failure(e.getMessage(), getProviderType().getCode());
        }
    }

    /**
     * Build the full messages list from request.
     */
    protected List<LlmMessage> buildMessages(LlmRequest request) {
        List<LlmMessage> messages = new ArrayList<>();
        if (request.getSystemPrompt() != null && !request.getSystemPrompt().isEmpty()) {
            messages.add(LlmMessage.system(request.getSystemPrompt()));
        }
        if (request.getMessages() != null) {
            messages.addAll(request.getMessages());
        }
        if (request.getPrompt() != null && !request.getPrompt().isEmpty()) {
            messages.add(LlmMessage.user(request.getPrompt()));
        }
        return messages;
    }

    /**
     * Build the JSON request body.
     */
    protected JSONObject buildRequestBody(LlmRequest request, List<LlmMessage> messages) {
        JSONObject body = new JSONObject();
        body.set("model", request.getModel() != null ? request.getModel() : config.getDefaultModel());
        body.set("temperature", request.getTemperature());

        if (request.getMaxTokens() != null) {
            body.set("max_tokens", request.getMaxTokens());
        } else if (config.getDefaultMaxTokens() != null) {
            body.set("max_tokens", config.getDefaultMaxTokens());
        }

        JSONArray messagesArray = new JSONArray();
        for (LlmMessage msg : messages) {
            JSONObject msgObj = new JSONObject();
            msgObj.set("role", msg.getRole().name().toLowerCase());
            msgObj.set("content", msg.getContent());
            messagesArray.add(msgObj);
        }
        body.set("messages", messagesArray);
        return body;
    }

    /**
     * Build the chat completions URL.
     */
    protected String buildChatUrl() {
        String host = config.getApiHost();
        if (!host.endsWith("/")) {
            host += "/";
        }
        return host + "chat/completions";
    }

    /**
     * Execute the HTTP request with headers.
     */
    protected HttpResponse executeRequest(String url, JSONObject body, int timeoutMs) {
        HttpRequest request = new HttpRequest(url)
                .method(Method.POST)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getApiKey())
                .body(body.toString())
                .timeout(timeoutMs);

        if (config.getCustomHeaders() != null) {
            config.getCustomHeaders().forEach(request::header);
        }
        return request.execute();
    }

    /**
     * Parse the API response (OpenAI format).
     */
    protected LlmResponse parseResponse(String responseBody) {
        try {
            JSONObject json = JSONUtil.parseObj(responseBody);
            if (json.containsKey("choices")) {
                JSONArray choices = json.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    if (message != null) {
                        String content = message.getStr("content", "");
                        LlmResponse.Usage usage = null;
                        if (json.containsKey("usage")) {
                            JSONObject usageObj = json.getJSONObject("usage");
                            usage = LlmResponse.Usage.builder()
                                    .promptTokens(usageObj.getInt("prompt_tokens", 0))
                                    .completionTokens(usageObj.getInt("completion_tokens", 0))
                                    .totalTokens(usageObj.getInt("total_tokens", 0))
                                    .build();
                        }
                        return LlmResponse.builder()
                                .success(true)
                                .content(content)
                                .model(json.getStr("model"))
                                .usage(usage)
                                .rawResponse(responseBody)
                                .build();
                    }
                }
            }
            return LlmResponse.failure("Unexpected response format", getProviderType().getCode());
        } catch (Exception e) {
            log.warn("Failed to parse response, returning raw", e);
            return LlmResponse.builder()
                    .success(true)
                    .content(responseBody)
                    .rawResponse(responseBody)
                    .build();
        }
    }

    /**
     * Map HTTP status code to user-friendly error message.
     * Inspired by AI-Gateway's error mapping.
     */
    protected String mapErrorMessage(int status, String body) {
        return switch (status) {
            case 400 -> "Bad request";
            case 401 -> "Authentication failed - check API key";
            case 403 -> "Forbidden - insufficient permissions";
            case 404 -> "Resource not found - check API host";
            case 429 -> "Rate limited - too many requests";
            case 500, 502, 503 -> "Server error - provider unavailable";
            default -> "HTTP " + status;
        };
    }

    /**
     * Check if an error is non-retryable (auth errors, bad requests).
     */
    protected boolean isNonRetryableError(LlmResponse response) {
        if (response.getErrorMessage() == null) return false;
        String msg = response.getErrorMessage().toLowerCase();
        return msg.contains("authentication failed") ||
               msg.contains("401") ||
               msg.contains("403") ||
               msg.contains("bad request") ||
               msg.contains("400");
    }
}
