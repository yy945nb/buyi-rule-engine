package com.ymware.gateway.mcp.protocol;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * McpError 工厂方法测试
 */
class McpErrorTest {

    @Test
    @DisplayName("parseError: code=-32700")
    void parseError() {
        McpError error = McpError.parseError("bad json");
        assertThat(error.getCode()).isEqualTo(-32700);
        assertThat(error.getMessage()).isEqualTo("bad json");
    }

    @Test
    @DisplayName("invalidRequest: code=-32600")
    void invalidRequest() {
        McpError error = McpError.invalidRequest("missing id");
        assertThat(error.getCode()).isEqualTo(-32600);
        assertThat(error.getMessage()).isEqualTo("missing id");
    }

    @Test
    @DisplayName("methodNotFound: code=-32601, message 包含方法名")
    void methodNotFound() {
        McpError error = McpError.methodNotFound("foo/bar");
        assertThat(error.getCode()).isEqualTo(-32601);
        assertThat(error.getMessage()).contains("foo/bar");
    }

    @Test
    @DisplayName("invalidParams: code=-32602")
    void invalidParams() {
        McpError error = McpError.invalidParams("bad param");
        assertThat(error.getCode()).isEqualTo(-32602);
        assertThat(error.getMessage()).isEqualTo("bad param");
    }

    @Test
    @DisplayName("internalError: code=-32603")
    void internalError() {
        McpError error = McpError.internalError("something broke");
        assertThat(error.getCode()).isEqualTo(-32603);
        assertThat(error.getMessage()).isEqualTo("something broke");
    }
}
