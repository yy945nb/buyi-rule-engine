package com.ymware.gateway.mcp.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * AbstractMcpProtocolAdapter 测试
 * <p>通过 TestableMcpProtocolAdapter 子类暴露基类方法进行测试。</p>
 */
class AbstractMcpProtocolAdapterTest {

    private ObjectMapper objectMapper;
    private TestableMcpProtocolAdapter adapter;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        adapter = new TestableMcpProtocolAdapter(objectMapper);
    }

    // ===================== parseRequest =====================

    @Nested
    @DisplayName("parseRequest 请求解析")
    class ParseRequestTests {

        @Test
        @DisplayName("正常 JSON-RPC 请求解析")
        void parseRequest_validJson() {
            String json = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\",\"params\":{}}";
            McpRequest request = adapter.parseRequest(json);
            assertThat(request).isNotNull();
            assertThat(request.getJsonrpc()).isEqualTo("2.0");
            assertThat(request.getId()).isEqualTo(1);
            assertThat(request.getMethod()).isEqualTo("initialize");
        }

        @Test
        @DisplayName("非法 JSON 抛出 IllegalArgumentException")
        void parseRequest_invalidJson() {
            assertThatThrownBy(() -> adapter.parseRequest("not json"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid JSON-RPC request");
        }

        @Test
        @DisplayName("带 params 的请求")
        void parseRequest_withParams() {
            String json = "{\"jsonrpc\":\"2.0\",\"id\":\"abc\",\"method\":\"tools/call\",\"params\":{\"name\":\"test\"}}";
            McpRequest request = adapter.parseRequest(json);
            assertThat(request.getMethod()).isEqualTo("tools/call");
            assertThat(request.getParams()).isNotNull();
        }
    }

    // ===================== buildInitializeResult =====================

    @Nested
    @DisplayName("buildInitializeResult 初始化结果")
    class BuildInitializeResultTests {

        @Test
        @DisplayName("tools 非空时 capabilities 包含 tools")
        void buildInitializeResult_withTools() {
            McpRequest request = createRequest(1, "initialize");
            McpToolDefinition tool = McpToolDefinition.builder().name("test").description("desc").build();

            McpResponse response = adapter.buildInitializeResult(request, List.of(tool));

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1);
            assertThat(response.isError()).isFalse();
            JsonNode result = response.getResult();
            assertThat(result).isNotNull();
            assertThat(result.has("capabilities")).isTrue();
            assertThat(result.get("capabilities").has("tools")).isTrue();
        }

        @Test
        @DisplayName("tools 为空时 capabilities.tools 为 null")
        void buildInitializeResult_emptyTools() {
            McpRequest request = createRequest(1, "initialize");

            McpResponse response = adapter.buildInitializeResult(request, Collections.emptyList());

            assertThat(response).isNotNull();
            assertThat(response.isError()).isFalse();
            JsonNode result = response.getResult();
            assertThat(result).isNotNull();
            assertThat(result.has("capabilities")).isTrue();
            assertThat(result.get("capabilities").get("tools").isNull()).isTrue();
        }
    }

    // ===================== buildToolsListResult =====================

    @Nested
    @DisplayName("buildToolsListResult 工具列表")
    class BuildToolsListResultTests {

        @Test
        @DisplayName("tool 有 inputSchema 时正确序列化")
        void buildToolsListResult_withInputSchema() {
            McpRequest request = createRequest(2, "tools/list");
            JsonNode schema = objectMapper.createObjectNode().put("type", "object");
            McpToolDefinition tool = McpToolDefinition.builder()
                    .name("test").description("desc").inputSchema(schema).build();

            McpResponse response = adapter.buildToolsListResult(request, List.of(tool));

            assertThat(response.isError()).isFalse();
            JsonNode tools = response.getResult().get("tools");
            assertThat(tools).hasSize(1);
            assertThat(tools.get(0).get("name").asText()).isEqualTo("test");
            assertThat(tools.get(0).has("inputSchema")).isTrue();
        }

        @Test
        @DisplayName("tool 无 inputSchema 时字段不出现")
        void buildToolsListResult_nullInputSchema() {
            McpRequest request = createRequest(2, "tools/list");
            McpToolDefinition tool = McpToolDefinition.builder()
                    .name("test").description("desc").build();

            McpResponse response = adapter.buildToolsListResult(request, List.of(tool));

            JsonNode tools = response.getResult().get("tools");
            assertThat(tools).hasSize(1);
            assertThat(tools.get(0).has("inputSchema")).isFalse();
        }

        @Test
        @DisplayName("空工具列表")
        void buildToolsListResult_emptyList() {
            McpRequest request = createRequest(2, "tools/list");

            McpResponse response = adapter.buildToolsListResult(request, Collections.emptyList());

            JsonNode tools = response.getResult().get("tools");
            assertThat(tools).hasSize(0);
        }
    }

    // ===================== buildToolCallResult =====================

    @Test
    @DisplayName("buildToolCallResult 序列化工具调用结果")
    void buildToolCallResult_text() {
        McpRequest request = createRequest(3, "tools/call");
        McpToolCallResult callResult = McpToolCallResult.text("hello");

        McpResponse response = adapter.buildToolCallResult(request, callResult);

        assertThat(response.isError()).isFalse();
        JsonNode content = response.getResult().get("content");
        assertThat(content).hasSize(1);
        assertThat(content.get(0).get("text").asText()).isEqualTo("hello");
    }

    // ===================== encodeSseEvent =====================

    @Nested
    @DisplayName("encodeSseEvent SSE 编码")
    class EncodeSseEventTests {

        @Test
        @DisplayName("正常响应编码为 JSON 字符串")
        void encodeSseEvent_success() {
            McpResponse response = McpResponse.success(1, objectMapper.createObjectNode().put("ok", true));

            String encoded = adapter.encodeSseEvent(response);

            assertThat(encoded).contains("\"jsonrpc\":\"2.0\"");
            assertThat(encoded).contains("\"ok\":true");
        }

        @Test
        @DisplayName("错误响应编码")
        void encodeSseEvent_errorResponse() {
            McpResponse response = McpResponse.error(1, -32600, "bad request");

            String encoded = adapter.encodeSseEvent(response);

            assertThat(encoded).contains("\"code\":-32600");
            assertThat(encoded).contains("bad request");
        }
    }

    // ===================== buildError =====================

    @Test
    @DisplayName("buildError 构建错误响应")
    void buildError() {
        McpResponse response = adapter.buildError(1, -32600, "Invalid request");

        assertThat(response.isError()).isTrue();
        assertThat(response.getError().getCode()).isEqualTo(-32600);
        assertThat(response.getError().getMessage()).isEqualTo("Invalid request");
    }

    // ===================== helpers =====================

    private McpRequest createRequest(Object id, String method) {
        McpRequest request = new McpRequest();
        request.setJsonrpc("2.0");
        request.setId(id);
        request.setMethod(method);
        return request;
    }

    /**
     * 空实现，仅暴露基类方法
     */
    private static class TestableMcpProtocolAdapter extends AbstractMcpProtocolAdapter {
        TestableMcpProtocolAdapter(ObjectMapper objectMapper) {
            super(objectMapper);
        }
    }
}
