package com.ymware.gateway.mcp.controller;

import com.ymware.gateway.common.result.R;
import com.ymware.gateway.mcp.mapper.McpToolMappingMapper;
import com.ymware.gateway.mcp.model.McpToolMappingDO;
import com.ymware.gateway.mcp.protocol.McpToolCallResult;
import com.ymware.gateway.mcp.protocol.McpToolDefinition;
import com.ymware.gateway.mcp.protocol.McpToolExecutionService;
import com.ymware.gateway.mcp.registry.InMemoryToolRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/mcp/tools")
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpToolAdminController {

    private final McpToolMappingMapper toolMappingMapper;
    private final McpToolExecutionService toolExecutionService;
    private final InMemoryToolRegistry toolRegistry;
    private final ObjectMapper objectMapper;

    public McpToolAdminController(McpToolMappingMapper toolMappingMapper,
                                  McpToolExecutionService toolExecutionService,
                                  InMemoryToolRegistry toolRegistry,
                                  ObjectMapper objectMapper) {
        this.toolMappingMapper = toolMappingMapper;
        this.toolExecutionService = toolExecutionService;
        this.toolRegistry = toolRegistry;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/add")
    public Mono<R<Void>> add(@RequestBody McpToolMappingDO record) {
        return Mono.fromRunnable(() -> {
            toolMappingMapper.insert(record);
            toolRegistry.refresh();
        }).subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @PostMapping("/update")
    public Mono<R<Void>> update(@RequestBody McpToolMappingDO record) {
        return Mono.fromRunnable(() -> {
            toolMappingMapper.update(record);
            toolRegistry.refresh();
        }).subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @PostMapping("/delete/{id}")
    public Mono<R<Void>> delete(@PathVariable Long id) {
        return Mono.fromRunnable(() -> {
            toolMappingMapper.deleteById(id);
            toolRegistry.refresh();
        }).subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @PostMapping("/list")
    public Mono<R<List<McpToolMappingDO>>> list(@RequestBody Map<String, String> req) {
        return Mono.fromCallable(() -> {
            String serviceId = req.get("serviceId");
            if (serviceId != null) {
                return toolMappingMapper.findByServiceId(serviceId);
            }
            return toolMappingMapper.findAllEnabled();
        }).subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @PostMapping("/test/{toolId}")
    public Mono<R<Map<String, Object>>> testTool(@PathVariable Long toolId, @RequestBody Map<String, Object> args) {
        return Mono.fromCallable(() -> {
            McpToolMappingDO mapping = toolMappingMapper.findById(toolId);
            if (mapping == null) {
                return Map.<String, Object>of("success", false, "error", "Tool not found");
            }

            McpToolDefinition tool = McpToolDefinition.builder()
                    .name(mapping.getToolName())
                    .serviceId(mapping.getServiceId())
                    .restEndpoint(mapping.getRestEndpoint())
                    .restMethod(mapping.getRestMethod())
                    .build();

            com.fasterxml.jackson.databind.JsonNode arguments = objectMapper.valueToTree(args);
            McpToolCallResult result = toolExecutionService.execute(tool, arguments);

            return Map.<String, Object>of(
                    "success", Boolean.TRUE.equals(result.getIsError()) ? false : true,
                    "result", result
            );
        }).subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }
}
