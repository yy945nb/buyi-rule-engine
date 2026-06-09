package com.ymware.engine.domain.ai.gateway.provider;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import com.ymware.engine.domain.ai.gateway.LlmProviderConfig;
import com.ymware.engine.domain.ai.gateway.LlmProviderType;
import com.ymware.engine.domain.ai.gateway.LlmRequest;

/**
 * Zhipu GLM provider (glm-4, glm-3-turbo, etc.).
 * Uses OpenAI-compatible API with Bearer token auth.
 * API: https://open.bigmodel.cn/api/paas/v4/
 */
public class ZhipuProvider extends AbstractLlmProvider {

    public ZhipuProvider(LlmProviderConfig config) {
        super(config);
    }

    @Override
    public LlmProviderType getProviderType() {
        return LlmProviderType.ZHIPU;
    }

    @Override
    protected String buildChatUrl() {
        String host = config.getApiHost();
        if (!host.endsWith("/")) {
            host += "/";
        }
        // Zhipu uses v4/chat/completions
        if (!host.contains("/v4/") && !host.contains("/v3/")) {
            host += "v4/";
        }
        return host + "chat/completions";
    }

    @Override
    protected HttpResponse executeRequest(String url, cn.hutool.json.JSONObject body, int timeoutMs) {
        // Zhipu uses standard Bearer token auth
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
}
