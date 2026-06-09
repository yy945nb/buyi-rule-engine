package com.ymware.gateway.mcp.protocol;

import com.ymware.gateway.mcp.registry.InMemoryToolRegistry;
import com.ymware.gateway.mcp.registry.ToolExecutor;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpProtocolHandler {

    private static final Logger log = LoggerFactory.getLogger(McpProtocolHandler.class);

    private final McpProtocolAdapter adapter;
    private final InMemoryToolRegistry toolRegistry;
    private final ToolExecutor toolExecutor;

    public McpProtocolHandler(McpProtocolAdapter adapter,
                              InMemoryToolRegistry toolRegistry,
                              ToolExecutor toolExecutor) {
        this.adapter = adapter;
        this.toolRegistry = toolRegistry;
        this.toolExecutor = toolExecutor;
    }

    public Flux<ServerSentEvent<String>> handleRequest(String serviceId, String rawBody, ServerWebExchange exchange) {
        McpRequest request;
        try {
            request = adapter.parseRequest(rawBody);
        } catch (Exception e) {
            log.error("Failed to parse MCP request: {}", e.getMessage());
            McpResponse errorResp = adapter.buildError(null, -32700, "Parse error: " + e.getMessage());
            return Flux.just(buildSseEvent(errorResp));
        }

        McpMethod method = McpMethod.fromMethodName(request.getMethod());
        if (method == null) {
            McpResponse errorResp = adapter.buildError(request.getId(), -32601,
                    "Method not found: " + request.getMethod());
            return Flux.just(buildSseEvent(errorResp));
        }

        return switch (method) {
            case INITIALIZE -> handleInitialize(serviceId, request);
            case TOOLS_LIST -> handleToolsList(serviceId, request);
            case TOOLS_CALL -> handleToolCall(serviceId, request);
            case PING -> handlePing(request);
            default -> {
                McpResponse errorResp = adapter.buildError(request.getId(), -32601,
                        "Method not implemented: " + request.getMethod());
                yield Flux.just(buildSseEvent(errorResp));
            }
        };
    }

    private Flux<ServerSentEvent<String>> handleInitialize(String serviceId, McpRequest request) {
        List<McpToolDefinition> tools = toolRegistry.getToolsForService(serviceId);
        McpResponse response = adapter.buildInitializeResult(request, tools);
        return Flux.just(buildSseEvent(response));
    }

    private Flux<ServerSentEvent<String>> handleToolsList(String serviceId, McpRequest request) {
        List<McpToolDefinition> tools = toolRegistry.getToolsForService(serviceId);
        McpResponse response = adapter.buildToolsListResult(request, tools);
        return Flux.just(buildSseEvent(response));
    }

    private Flux<ServerSentEvent<String>> handleToolCall(String serviceId, McpRequest request) {
        McpToolCallParams params;
        try {
            params = parseToolCallParams(request.getParams());
        } catch (Exception e) {
            McpResponse errorResp = adapter.buildError(request.getId(), -32602,
                    "Invalid params: " + e.getMessage());
            return Flux.just(buildSseEvent(errorResp));
        }

        McpToolDefinition tool = toolRegistry.getTool(serviceId, params.getName());
        if (tool == null) {
            McpResponse errorResp = adapter.buildError(request.getId(), -32602,
                    "Tool not found: " + params.getName());
            return Flux.just(buildSseEvent(errorResp));
        }

        try {
            McpToolCallResult result = toolExecutor.execute(tool, params.getArguments());
            McpResponse response = adapter.buildToolCallResult(request, result);
            return Flux.just(buildSseEvent(response));
        } catch (Exception e) {
            log.error("Tool execution failed for {}: {}", params.getName(), e.getMessage());
            McpResponse errorResp = adapter.buildError(request.getId(), -32000,
                    "Tool execution error: " + e.getMessage());
            return Flux.just(buildSseEvent(errorResp));
        }
    }

    private Flux<ServerSentEvent<String>> handlePing(McpRequest request) {
        McpResponse response = McpResponse.success(request.getId(), new com.fasterxml.jackson.databind.node.ObjectNode(
                com.fasterxml.jackson.databind.node.JsonNodeFactory.instance));
        return Flux.just(buildSseEvent(response));
    }

    private McpToolCallParams parseToolCallParams(JsonNode params) {
        if (params == null) {
            throw new IllegalArgumentException("params is required");
        }
        McpToolCallParams result = new McpToolCallParams();
        JsonNode nameNode = params.get("name");
        if (nameNode == null || !nameNode.isTextual()) {
            throw new IllegalArgumentException("params.name is required");
        }
        result.setName(nameNode.asText());
        result.setArguments(params.get("arguments"));
        return result;
    }

    private ServerSentEvent<String> buildSseEvent(McpResponse response) {
        String encoded = adapter.encodeSseEvent(response);
        return ServerSentEvent.<String>builder()
                .event("message")
                .data(encoded)
                .build();
    }
}
