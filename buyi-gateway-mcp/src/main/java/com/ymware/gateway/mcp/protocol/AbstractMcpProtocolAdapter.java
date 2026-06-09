package com.ymware.gateway.mcp.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Abstract base for MCP protocol adapters. Provides shared JSON utilities.
 */
public abstract class AbstractMcpProtocolAdapter implements McpProtocolAdapter {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final ObjectMapper objectMapper;

    protected AbstractMcpProtocolAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public McpRequest parseRequest(String rawBody) {
        try {
            return objectMapper.readValue(rawBody, McpRequest.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse MCP request: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JSON-RPC request", e);
        }
    }

    @Override
    public McpResponse buildInitializeResult(McpRequest request, List<McpToolDefinition> tools) {
        McpInitializeResult initResult = McpInitializeResult.create(
                "ai-gateway", "1.0.0", !tools.isEmpty());
        JsonNode resultNode = objectMapper.valueToTree(initResult);
        return McpResponse.success(request.getId(), resultNode);
    }

    @Override
    public McpResponse buildToolsListResult(McpRequest request, List<McpToolDefinition> tools) {
        ObjectNode result = objectMapper.createObjectNode();
        ArrayNode toolsArray = objectMapper.createArrayNode();
        for (McpToolDefinition tool : tools) {
            ObjectNode toolNode = objectMapper.createObjectNode();
            toolNode.put("name", tool.getName());
            toolNode.put("description", tool.getDescription());
            if (tool.getInputSchema() != null) {
                toolNode.set("inputSchema", tool.getInputSchema());
            }
            toolsArray.add(toolNode);
        }
        result.set("tools", toolsArray);
        return McpResponse.success(request.getId(), result);
    }

    @Override
    public McpResponse buildToolCallResult(McpRequest request, McpToolCallResult callResult) {
        JsonNode resultNode = objectMapper.valueToTree(callResult);
        return McpResponse.success(request.getId(), resultNode);
    }

    @Override
    public String encodeSseEvent(McpResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            log.error("Failed to encode MCP response: {}", e.getMessage());
            return "{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-32603,\"message\":\"Internal error\"}}";
        }
    }

    @Override
    public McpResponse buildError(Object id, int code, String message) {
        return McpResponse.error(id, code, message);
    }
}
