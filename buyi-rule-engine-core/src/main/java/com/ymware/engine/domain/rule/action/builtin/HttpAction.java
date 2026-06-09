package com.ymware.engine.domain.rule.action.builtin;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ymware.engine.domain.rule.action.Action;
import com.ymware.engine.domain.rule.action.ActionResult;
import com.ymware.engine.domain.rule.model.ActionException;
import com.ymware.engine.domain.rule.model.ExecutionContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * HTTP Action - calls external APIs within rule execution.
 * Type: "HTTP"
 *
 * Config keys:
 * - url: the endpoint URL (required, supports ${variable} substitution)
 * - method: HTTP method (optional, default GET)
 * - headers: JSON string of headers (optional)
 * - body: request body string (optional, supports ${variable} substitution)
 * - timeout: timeout in ms (optional, default 30000)
 * - outputVariable: context variable to store the response (optional, default "httpResult")
 */
@Slf4j
public class HttpAction implements Action {

    private final String actionId;
    private final String url;
    private final String method;
    private final String headers;
    private final String body;
    private final int timeout;
    private final String outputVariable;

    public HttpAction(String actionId, Map<String, Object> config) {
        this.actionId = actionId;
        this.url = (String) config.getOrDefault("url", "");
        this.method = (String) config.getOrDefault("method", "GET");
        this.headers = (String) config.get("headers");
        this.body = (String) config.get("body");
        this.timeout = config.containsKey("timeout") ?
                Integer.parseInt(config.get("timeout").toString()) : 30000;
        this.outputVariable = (String) config.getOrDefault("outputVariable", "httpResult");
    }

    @Override
    public ActionResult execute(ExecutionContext context) throws ActionException {
        try {
            String resolvedUrl = context.substituteVariables(url);
            String resolvedBody = body != null ? context.substituteVariables(body) : null;

            log.debug("[HttpAction] Executing: {} {}", method, resolvedUrl);

            HttpRequest request = new HttpRequest(resolvedUrl)
                    .method(Method.valueOf(method.toUpperCase()))
                    .timeout(timeout);

            // Set headers
            if (headers != null && !headers.isBlank()) {
                String resolvedHeaders = context.substituteVariables(headers);
                JSONObject headerObj = JSONUtil.parseObj(resolvedHeaders);
                headerObj.forEach((k, v) -> request.header(k, v.toString()));
            }

            // Set body
            if (resolvedBody != null && !resolvedBody.isBlank()) {
                request.body(resolvedBody);
            }

            HttpResponse response = null;
            try {
                response = request.execute();

                String responseBody = response.body();
                int statusCode = response.getStatus();

                // Store in context
                context.setVariable(outputVariable, responseBody);
                context.setVariable(outputVariable + "_status", statusCode);
                context.setVariable(outputVariable + "_headers", response.headers());

                log.debug("[HttpAction] Response: status={}, bodyLength={}", statusCode,
                        responseBody != null ? responseBody.length() : 0);

                if (statusCode >= 200 && statusCode < 300) {
                    return ActionResult.success(responseBody);
                } else {
                    return ActionResult.failure("HTTP " + statusCode + ": " + responseBody);
                }
            } finally {
                if (response != null) {
                    try { response.close(); } catch (Exception ignored) {}
                }
            }

        } catch (Exception e) {
            log.error("[HttpAction] Execution error", e);
            throw new ActionException("HTTP action failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String getType() {
        return "HTTP";
    }

    @Override
    public String getActionId() {
        return actionId;
    }

}
