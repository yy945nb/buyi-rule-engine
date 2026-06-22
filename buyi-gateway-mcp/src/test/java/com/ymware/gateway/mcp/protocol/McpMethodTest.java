package com.ymware.gateway.mcp.protocol;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * McpMethod 枚举测试
 */
class McpMethodTest {

    @Test
    @DisplayName("已知方法名返回对应枚举")
    void fromMethodName_known() {
        assertThat(McpMethod.fromMethodName("initialize")).isEqualTo(McpMethod.INITIALIZE);
        assertThat(McpMethod.fromMethodName("tools/list")).isEqualTo(McpMethod.TOOLS_LIST);
        assertThat(McpMethod.fromMethodName("tools/call")).isEqualTo(McpMethod.TOOLS_CALL);
        assertThat(McpMethod.fromMethodName("ping")).isEqualTo(McpMethod.PING);
        assertThat(McpMethod.fromMethodName("resources/list")).isEqualTo(McpMethod.RESOURCES_LIST);
    }

    @Test
    @DisplayName("未知方法名返回 null")
    void fromMethodName_unknown() {
        assertThat(McpMethod.fromMethodName("unknown/method")).isNull();
        assertThat(McpMethod.fromMethodName("")).isNull();
        assertThat(McpMethod.fromMethodName(null)).isNull();
    }

    @Test
    @DisplayName("所有枚举值都有 methodName")
    void allEnumValues_haveMethodName() {
        for (McpMethod method : McpMethod.values()) {
            assertThat(method.getMethodName()).isNotBlank();
        }
    }
}
