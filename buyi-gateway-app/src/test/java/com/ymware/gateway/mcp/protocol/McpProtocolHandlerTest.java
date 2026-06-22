package com.ymware.gateway.mcp.protocol;

import com.ymware.gateway.mcp.registry.InMemoryToolRegistry;
import com.ymware.gateway.mcp.registry.ToolExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.ServerSentEvent;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * McpProtocolHandler 测试 — 覆盖 JSON-RPC 方法分发和错误码
 */
@ExtendWith(MockitoExtension.class)
class McpProtocolHandlerTest {

    @Mock
    private InMemoryToolRegistry toolRegistry;
    @Mock
    private ToolExecutor toolExecutor;

    private McpProtocolHandler handler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        McpProtocolAdapterImpl adapter = new McpProtocolAdapterImpl(objectMapper);
        handler = new McpProtocolHandler(adapter, toolRegistry, toolExecutor);
    }

    @Nested
    @DisplayName("initialize 方法")
    class InitializeTests {

        @Test
        @DisplayName("返回初始化结果")
        void handleInitialize() {
            when(toolRegistry.getToolsForService("svc-1")).thenReturn(Collections.emptyList());

            String rawBody = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\",\"params\":{}}";
            StepVerifier.create(handler.handleRequest("svc-1", rawBody, null))
                    .assertNext(sse -> {
                        assertThat(sse.event()).isEqualTo("message");
                        assertThat(sse.data()).contains("\"result\"");
                        assertThat(sse.data()).contains("\"capabilities\"");
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("tools/list 方法")
    class ToolsListTests {

        @Test
        @DisplayName("返回工具列表")
        void handleToolsList() {
            McpToolDefinition tool = McpToolDefinition.builder()
                    .name("test-tool").description("A test tool").build();
            when(toolRegistry.getToolsForService("svc-1")).thenReturn(List.of(tool));

            String rawBody = "{\"jsonrpc\":\"2.0\",\"id\":2,\"method\":\"tools/list\",\"params\":{}}";
            StepVerifier.create(handler.handleRequest("svc-1", rawBody, null))
                    .assertNext(sse -> {
                        assertThat(sse.data()).contains("test-tool");
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("tools/call 方法")
    class ToolsCallTests {

        @Test
        @DisplayName("调用工具并返回结果")
        void handleToolCall() {
            McpToolDefinition tool = McpToolDefinition.builder().name("echo").build();
            when(toolRegistry.getTool("svc-1", "echo")).thenReturn(tool);
            when(toolExecutor.execute(eq(tool), any())).thenReturn(McpToolCallResult.text("hello"));

            ObjectNode params = objectMapper.createObjectNode().put("name", "echo");
            String rawBody = "{\"jsonrpc\":\"2.0\",\"id\":3,\"method\":\"tools/call\",\"params\":" + params + "}";

            StepVerifier.create(handler.handleRequest("svc-1", rawBody, null))
                    .assertNext(sse -> {
                        assertThat(sse.data()).contains("hello");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("工具不存在 → INVALID_PARAMS 错误")
        void handleToolCall_toolNotFound() {
            when(toolRegistry.getTool("svc-1", "nonexistent")).thenReturn(null);

            ObjectNode params = objectMapper.createObjectNode().put("name", "nonexistent");
            String rawBody = "{\"jsonrpc\":\"2.0\",\"id\":3,\"method\":\"tools/call\",\"params\":" + params + "}";

            StepVerifier.create(handler.handleRequest("svc-1", rawBody, null))
                    .assertNext(sse -> {
                        assertThat(sse.data()).contains("-32602");
                        assertThat(sse.data()).contains("Tool not found");
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("ping 方法")
    class PingTests {

        @Test
        @DisplayName("返回 pong")
        void handlePing() {
            String rawBody = "{\"jsonrpc\":\"2.0\",\"id\":4,\"method\":\"ping\"}";
            StepVerifier.create(handler.handleRequest("svc-1", rawBody, null))
                    .assertNext(sse -> {
                        assertThat(sse.data()).contains("\"result\"");
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("错误处理")
    class ErrorTests {

        @Test
        @DisplayName("未知方法 → METHOD_NOT_FOUND 错误")
        void unknownMethod() {
            String rawBody = "{\"jsonrpc\":\"2.0\",\"id\":5,\"method\":\"unknown/method\"}";
            StepVerifier.create(handler.handleRequest("svc-1", rawBody, null))
                    .assertNext(sse -> {
                        assertThat(sse.data()).contains("-32601");
                        assertThat(sse.data()).contains("Method not found");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("非法 JSON → PARSE_ERROR")
        void invalidJson() {
            String rawBody = "not json";
            StepVerifier.create(handler.handleRequest("svc-1", rawBody, null))
                    .assertNext(sse -> {
                        assertThat(sse.data()).contains("-32700");
                    })
                    .verifyComplete();
        }
    }
}
