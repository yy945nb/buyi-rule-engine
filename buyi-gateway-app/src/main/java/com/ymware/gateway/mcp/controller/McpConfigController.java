package com.ymware.gateway.mcp.controller;

import com.ymware.gateway.common.result.R;
import com.ymware.gateway.mcp.service.McpConfigGeneratorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@RestController
@RequestMapping("/admin/mcp/config")
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpConfigController {

    private final McpConfigGeneratorService configGenerator;

    public McpConfigController(McpConfigGeneratorService configGenerator) {
        this.configGenerator = configGenerator;
    }

    @GetMapping("/generate/{serviceId}")
    public Mono<R<Map<String, Object>>> generateConfig(
            @PathVariable String serviceId,
            @RequestParam String authKey,
            @RequestParam(defaultValue = "yaml") String format) {
        return Mono.fromCallable(() -> {
            return switch (format.toLowerCase()) {
                case "json" -> configGenerator.generateSpringAiConfig(serviceId, authKey);
                case "claude" -> configGenerator.generateClaudeDesktopConfig(serviceId, authKey);
                default -> Map.<String, Object>of("yaml", configGenerator.generateYaml(serviceId, authKey));
            };
        }).subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }
}
