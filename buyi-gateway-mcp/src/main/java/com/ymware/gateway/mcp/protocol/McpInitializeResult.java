package com.ymware.gateway.mcp.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP initialize response result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpInitializeResult {

    private String protocolVersion;
    private ServerCapabilities capabilities;
    private ServerInfo serverInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServerCapabilities {
        private JsonNode tools;
        private JsonNode resources;
        private JsonNode prompts;
        private JsonNode logging;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServerInfo {
        private String name;
        private String version;
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static McpInitializeResult create(String serverName, String serverVersion, boolean hasTools) {
        ObjectNode toolsNode = MAPPER.createObjectNode();
        if (hasTools) {
            toolsNode.put("listChanged", false);
        }

        return McpInitializeResult.builder()
                .protocolVersion("2024-11-05")
                .capabilities(ServerCapabilities.builder()
                        .tools(hasTools ? toolsNode : null)
                        .build())
                .serverInfo(ServerInfo.builder()
                        .name(serverName)
                        .version(serverVersion)
                        .build())
                .build();
    }
}
