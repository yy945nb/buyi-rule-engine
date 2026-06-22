package com.ymware.gateway.mcp.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MCP tools/call result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpToolCallResult {

    private List<ContentItem> content;
    private Boolean isError;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContentItem {
        private String type;
        private String text;
        private String mimeType;
        private JsonNode data;
    }

    public static McpToolCallResult text(String text) {
        return McpToolCallResult.builder()
                .content(List.of(ContentItem.builder().type("text").text(text).build()))
                .isError(false)
                .build();
    }

    public static McpToolCallResult error(String errorMessage) {
        return McpToolCallResult.builder()
                .content(List.of(ContentItem.builder().type("text").text(errorMessage).build()))
                .isError(true)
                .build();
    }

    public static McpToolCallResult json(JsonNode json) {
        return McpToolCallResult.builder()
                .content(List.of(ContentItem.builder()
                        .type("text")
                        .text(json.toString())
                        .build()))
                .isError(false)
                .build();
    }
}
