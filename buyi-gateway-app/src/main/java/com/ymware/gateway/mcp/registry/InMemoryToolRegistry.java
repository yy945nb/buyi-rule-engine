package com.ymware.gateway.mcp.registry;

import com.ymware.gateway.mcp.mapper.McpToolMappingMapper;
import com.ymware.gateway.mcp.model.McpToolMappingDO;
import com.ymware.gateway.mcp.protocol.McpToolDefinition;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class InMemoryToolRegistry implements ToolRegistry {

    private static final Logger log = LoggerFactory.getLogger(InMemoryToolRegistry.class);

    private final McpToolMappingMapper toolMappingMapper;
    private final ObjectMapper objectMapper;

    // serviceId -> (toolName -> ToolDefinition)
    private final Map<String, Map<String, McpToolDefinition>> toolsByService = new ConcurrentHashMap<>();

    public InMemoryToolRegistry(McpToolMappingMapper toolMappingMapper, ObjectMapper objectMapper) {
        this.toolMappingMapper = toolMappingMapper;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        refresh();
    }

    @Override
    public List<McpToolDefinition> getToolsForService(String serviceId) {
        Map<String, McpToolDefinition> tools = toolsByService.get(serviceId);
        if (tools == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(tools.values());
    }

    @Override
    public McpToolDefinition getTool(String serviceId, String toolName) {
        Map<String, McpToolDefinition> tools = toolsByService.get(serviceId);
        if (tools == null) {
            return null;
        }
        return tools.get(toolName);
    }

    @Override
    public void registerTool(String serviceId, McpToolDefinition tool) {
        toolsByService.computeIfAbsent(serviceId, k -> new ConcurrentHashMap<>())
                .put(tool.getName(), tool);
    }

    @Override
    public void removeTool(String serviceId, String toolName) {
        Map<String, McpToolDefinition> tools = toolsByService.get(serviceId);
        if (tools != null) {
            tools.remove(toolName);
        }
    }

    @Override
    public void refresh() {
        try {
            List<McpToolMappingDO> allMappings = toolMappingMapper.findAllEnabled();
            Map<String, Map<String, McpToolDefinition>> newTools = new ConcurrentHashMap<>();

            for (McpToolMappingDO mapping : allMappings) {
                McpToolDefinition tool = convertToDefinition(mapping);
                newTools.computeIfAbsent(tool.getServiceId(), k -> new ConcurrentHashMap<>())
                        .put(tool.getName(), tool);
            }

            toolsByService.clear();
            toolsByService.putAll(newTools);
            log.info("Loaded {} MCP tool definitions across {} services",
                    allMappings.size(), newTools.size());
        } catch (Exception e) {
            log.error("Failed to refresh tool registry: {}", e.getMessage());
        }
    }

    private McpToolDefinition convertToDefinition(McpToolMappingDO mapping) {
        McpToolDefinition tool = new McpToolDefinition();
        tool.setName(mapping.getToolName());
        tool.setDescription(mapping.getToolDescription());
        tool.setServiceId(mapping.getServiceId());
        tool.setRestEndpoint(mapping.getRestEndpoint());
        tool.setRestMethod(mapping.getRestMethod());

        // Parse JSON fields
        if (mapping.getInputSchemaJson() != null) {
            try {
                tool.setInputSchema(objectMapper.readTree(mapping.getInputSchemaJson()));
            } catch (Exception e) {
                log.warn("Failed to parse inputSchemaJson for tool {}: {}", mapping.getToolName(), e.getMessage());
            }
        }

        if (mapping.getRestHeadersJson() != null) {
            try {
                tool.setRestHeaderMapping(objectMapper.readValue(mapping.getRestHeadersJson(),
                        new TypeReference<Map<String, String>>() {}));
            } catch (Exception e) {
                log.warn("Failed to parse restHeadersJson for tool {}: {}", mapping.getToolName(), e.getMessage());
            }
        }

        if (mapping.getRestParamMappingJson() != null) {
            try {
                tool.setRestQueryParamMapping(objectMapper.readValue(mapping.getRestParamMappingJson(),
                        new TypeReference<Map<String, String>>() {}));
            } catch (Exception e) {
                log.warn("Failed to parse restParamMappingJson for tool {}: {}", mapping.getToolName(), e.getMessage());
            }
        }

        if (mapping.getResponseMappingJson() != null) {
            try {
                tool.setResponseMapping(objectMapper.readValue(mapping.getResponseMappingJson(),
                        new TypeReference<Map<String, String>>() {}));
            } catch (Exception e) {
                log.warn("Failed to parse responseMappingJson for tool {}: {}", mapping.getToolName(), e.getMessage());
            }
        }

        return tool;
    }
}
