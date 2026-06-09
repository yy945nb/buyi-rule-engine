package com.ymware.engine.controller.api;

import com.ymware.engine.domain.ai.gateway.*;
import com.ymware.engine.model.response.RestResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * LLM调用API - 对外暴露AI Gateway能力
 */
@Slf4j
@Tag(name = "LLM调用")
@RestController
@RequestMapping("api/llm")
public class LlmController {

    @Resource
    private LlmGatewayService llmGatewayService;

    /**
     * 调用LLM（自动路由到合适的Provider）
     *
     * @param request LLM请求
     * @return LLM响应
     */
    @Operation(summary = "调用LLM")
    @PostMapping("chat")
    public RestResult<Map<String, Object>> chat(@RequestBody LlmChatRequest request) {
        try {
            LlmRequest llmRequest = LlmRequest.builder()
                    .model(request.getModel())
                    .prompt(request.getPrompt())
                    .systemPrompt(request.getSystemPrompt())
                    .temperature(request.getTemperature() != null ? request.getTemperature() : 0.7)
                    .maxTokens(request.getMaxTokens())
                    .timeoutMs(request.getTimeoutMs() != null ? request.getTimeoutMs() : 60000)
                    .build();

            LlmResponse response = llmGatewayService.chat(llmRequest);

            Map<String, Object> data = new HashMap<>();
            data.put("success", response.isSuccess());
            data.put("content", response.getContent());
            data.put("model", response.getModel());
            data.put("provider", response.getProviderType());
            data.put("latencyMs", response.getLatencyMs());
            if (response.getUsage() != null) {
                data.put("promptTokens", response.getUsage().getPromptTokens());
                data.put("completionTokens", response.getUsage().getCompletionTokens());
                data.put("totalTokens", response.getUsage().getTotalTokens());
            }
            if (!response.isSuccess()) {
                data.put("error", response.getErrorMessage());
            }

            return response.isSuccess() ? RestResult.ok(data) : RestResult.failed(500, response.getErrorMessage(), data);
        } catch (Exception e) {
            log.error("LLM chat failed", e);
            return RestResult.failed(500, "调用失败: " + e.getMessage());
        }
    }

    /**
     * 获取Gateway统计信息
     */
    @Operation(summary = "获取LLM统计")
    @GetMapping("stats")
    public RestResult<Map<String, LlmGatewayService.ProviderStats>> stats() {
        return RestResult.ok(llmGatewayService.getStats());
    }

    /**
     * 获取已注册的Provider列表
     */
    @Operation(summary = "获取Provider列表")
    @GetMapping("providers")
    public RestResult<Map<String, Object>> providers() {
        Map<String, Object> data = new HashMap<>();
        LlmProviderRegistry registry = llmGatewayService.getRegistry();
        data.put("providers", registry.getProviderIds());
        return RestResult.ok(data);
    }

    /**
     * LLM聊天请求体
     */
    @lombok.Data
    public static class LlmChatRequest {
        /** 模型名称（如 gpt-4, deepseek-chat, qwen-turbo） */
        private String model;
        /** 用户提示词 */
        private String prompt;
        /** 系统提示词 */
        private String systemPrompt;
        /** 温度 (0.0-2.0) */
        private Double temperature;
        /** 最大输出token数 */
        private Integer maxTokens;
        /** 超时毫秒数 */
        private Integer timeoutMs;
    }
}
