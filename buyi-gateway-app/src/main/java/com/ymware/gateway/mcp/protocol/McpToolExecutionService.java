package com.ymware.gateway.mcp.protocol;

import com.ymware.gateway.mcp.registry.ToolExecutor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.Map;

@Service
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpToolExecutionService implements ToolExecutor {

    private static final Logger log = LoggerFactory.getLogger(McpToolExecutionService.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public McpToolExecutionService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public McpToolCallResult execute(McpToolDefinition tool, JsonNode arguments) {
        try {
            String url = buildUrl(tool, arguments);
            HttpMethod method = HttpMethod.valueOf(tool.getRestMethod() != null ? tool.getRestMethod() : "GET");

            log.debug("Executing tool {} -> {} {}", tool.getName(), method, url);

            WebClient.RequestBodySpec requestSpec = webClient.method(method)
                    .uri(url)
                    .headers(headers -> {
                        // Apply static headers from tool definition
                        if (tool.getRestHeaderMapping() != null) {
                            tool.getRestHeaderMapping().forEach(headers::set);
                        }
                    });

            Mono<String> responseMono;
            if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
                JsonNode body = buildRequestBody(tool, arguments);
                responseMono = requestSpec
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body.toString())
                        .retrieve()
                        .bodyToMono(String.class);
            } else {
                responseMono = requestSpec
                        .retrieve()
                        .bodyToMono(String.class);
            }

            String responseBody = responseMono.block();
            return buildToolResult(tool, responseBody);
        } catch (Exception e) {
            log.error("Tool execution error for {}: {}", tool.getName(), e.getMessage());
            return McpToolCallResult.error("Tool execution failed: " + e.getMessage());
        }
    }

    private String buildUrl(McpToolDefinition tool, JsonNode arguments) {
        String url = tool.getRestEndpoint();

        // Replace path parameters: /api/products/{id} ← arguments.id
        if (tool.getRestPathParamMapping() != null && arguments != null) {
            for (Map.Entry<String, String> entry : tool.getRestPathParamMapping().entrySet()) {
                String paramName = entry.getKey();   // e.g., "id"
                String argPath = entry.getValue();   // e.g., "id" (path in arguments)
                JsonNode argValue = resolveJsonPath(arguments, argPath);
                if (argValue != null) {
                    url = url.replace("{" + paramName + "}", argValue.asText());
                }
            }
        }

        // Add query parameters
        if (tool.getRestQueryParamMapping() != null && arguments != null) {
            StringBuilder queryBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : tool.getRestQueryParamMapping().entrySet()) {
                String argName = entry.getKey();
                String paramType = entry.getValue(); // "query", "path", "body"
                if ("query".equals(paramType)) {
                    JsonNode argValue = arguments.get(argName);
                    if (argValue != null) {
                        if (queryBuilder.length() > 0) queryBuilder.append("&");
                        queryBuilder.append(argName).append("=").append(argValue.asText());
                    }
                }
            }
            if (queryBuilder.length() > 0) {
                url += (url.contains("?") ? "&" : "?") + queryBuilder.toString();
            }
        }

        return url;
    }

    private JsonNode buildRequestBody(McpToolDefinition tool, JsonNode arguments) {
        if (arguments == null) {
            return objectMapper.createObjectNode();
        }
        // If there's a body param mapping, extract specific fields
        if (tool.getRestQueryParamMapping() != null) {
            ObjectNode body = objectMapper.createObjectNode();
            boolean hasBodyMapping = false;
            for (Map.Entry<String, String> entry : tool.getRestQueryParamMapping().entrySet()) {
                if ("body".equals(entry.getValue())) {
                    JsonNode value = arguments.get(entry.getKey());
                    if (value != null) {
                        body.set(entry.getKey(), value);
                        hasBodyMapping = true;
                    }
                }
            }
            if (hasBodyMapping) return body;
        }
        // Default: send entire arguments as body
        return arguments;
    }

    private McpToolCallResult buildToolResult(McpToolDefinition tool, String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return McpToolCallResult.text("Empty response");
        }

        // Try to apply response mapping if configured
        if (tool.getResponseMapping() != null) {
            try {
                JsonNode responseJson = objectMapper.readTree(responseBody);
                ObjectNode result = objectMapper.createObjectNode();
                for (Map.Entry<String, String> entry : tool.getResponseMapping().entrySet()) {
                    JsonNode value = resolveJsonPath(responseJson, entry.getValue());
                    if (value != null) {
                        result.set(entry.getKey(), value);
                    }
                }
                return McpToolCallResult.json(result);
            } catch (Exception e) {
                log.debug("Response mapping failed, returning raw: {}", e.getMessage());
            }
        }

        // Return raw response as text
        return McpToolCallResult.text(responseBody);
    }

    private JsonNode resolveJsonPath(JsonNode node, String path) {
        if (node == null || path == null) return null;
        String[] parts = path.split("\\.");
        JsonNode current = node;
        for (String part : parts) {
            if (current == null) return null;
            if (part.contains("[") && part.endsWith("]")) {
                String fieldName = part.substring(0, part.indexOf('['));
                int index = Integer.parseInt(part.substring(part.indexOf('[') + 1, part.length() - 1));
                current = current.get(fieldName);
                if (current != null && current.isArray() && index < current.size()) {
                    current = current.get(index);
                } else {
                    return null;
                }
            } else {
                current = current.get(part);
            }
        }
        return current;
    }
}
