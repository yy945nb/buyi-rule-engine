package com.ymware.gateway.mcp.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * MCP tool definition. For transparent proxy, only name/description/inputSchema are used.
 * For REST->MCP conversion, rest* fields provide the mapping metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpToolDefinition {

    private String name;
    private String description;
    private String serviceId;
    private JsonNode inputSchema;

    // REST->MCP mapping fields (only used when service_type = PROTOCOL_PARSE)
    private String restEndpoint;
    private String restMethod;
    private Map<String, String> restHeaderMapping;
    private Map<String, String> restPathParamMapping;
    private Map<String, String> restQueryParamMapping;
    private Map<String, String> responseMapping;
}
