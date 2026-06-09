package com.ymware.gateway.mcp.protocol;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MCP JSON-RPC method names.
 */
public enum McpMethod {

    INITIALIZE("initialize"),
    INITIALIZED("initialized"),
    TOOLS_LIST("tools/list"),
    TOOLS_CALL("tools/call"),
    PING("ping"),
    RESOURCES_LIST("resources/list"),
    RESOURCES_READ("resources/read"),
    RESOURCES_SUBSCRIBE("resources/subscribe"),
    RESOURCES_UNSUBSCRIBE("resources/unsubscribe"),
    PROMPTS_LIST("prompts/list"),
    PROMPTS_GET("prompts/get"),
    LOGGING_SET_LEVEL("logging/setLevel"),
    COMPLETION_COMPLETE("completion/complete");

    private final String methodName;

    private static final Map<String, McpMethod> BY_METHOD =
            Stream.of(values()).collect(Collectors.toMap(McpMethod::getMethodName, Function.identity()));

    McpMethod(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public static McpMethod fromMethodName(String methodName) {
        return BY_METHOD.get(methodName);
    }
}
