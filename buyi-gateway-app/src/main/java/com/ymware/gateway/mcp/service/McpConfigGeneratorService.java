package com.ymware.gateway.mcp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpConfigGeneratorService {

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${gateway.base-url:http://localhost:${server.port}}")
    private String baseUrl;

    public Map<String, Object> generateSpringAiConfig(String serviceId, String authKey) {
        String sseUrl = baseUrl + "/mcp/" + serviceId + "/sse?key=" + authKey;

        return Map.of(
                "spring", Map.of(
                        "ai", Map.of(
                                "mcp", Map.of(
                                        "client", Map.of(
                                                "sse", Map.of(
                                                        "url", sseUrl
                                                )
                                        )
                                )
                        )
                )
        );
    }

    public Map<String, Object> generateClaudeDesktopConfig(String serviceId, String authKey) {
        String sseUrl = baseUrl + "/mcp/" + serviceId + "/sse?key=" + authKey;

        return Map.of(
                "mcpServers", Map.of(
                        serviceId, Map.of(
                                "transport", "sse",
                                "url", sseUrl
                        )
                )
        );
    }

    public String generateYaml(String serviceId, String authKey) {
        String sseUrl = baseUrl + "/mcp/" + serviceId + "/sse?key=" + authKey;
        return """
                spring:
                  ai:
                    mcp:
                      client:
                        sse:
                          url: %s
                """.formatted(sseUrl);
    }

    public String generateJson(String serviceId, String authKey) {
        String sseUrl = baseUrl + "/mcp/" + serviceId + "/sse?key=" + authKey;
        return """
                {
                  "spring": {
                    "ai": {
                      "mcp": {
                        "client": {
                          "sse": {
                            "url": "%s"
                          }
                        }
                      }
                    }
                  }
                }
                """.formatted(sseUrl);
    }
}
