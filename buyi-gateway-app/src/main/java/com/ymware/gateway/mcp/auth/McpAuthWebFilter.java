package com.ymware.gateway.mcp.auth;

import com.ymware.gateway.mcp.config.McpProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 50)
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpAuthWebFilter implements WebFilter {

    private static final String PATH_PREFIX = "/mcp/";
    private static final String ATTR_AUTH_KEY = "mcpAuthKey";
    private static final String ATTR_SERVICE_ID = "mcpServiceId";

    private final McpAuthService authService;
    private final McpProperties properties;
    private final ObjectMapper objectMapper;

    public McpAuthWebFilter(McpAuthService authService, McpProperties properties, ObjectMapper objectMapper) {
        this.authService = authService;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Only intercept /mcp/**
        if (!path.startsWith(PATH_PREFIX)) {
            return chain.filter(exchange);
        }

        // Whitelist check
        if (authService.isWhitelistedPath(path)) {
            return chain.filter(exchange);
        }

        // Auth disabled check
        if (!properties.getAuth().isEnabled()) {
            return chain.filter(exchange);
        }

        // Extract auth key: query param > X-Api-Key header > Authorization Bearer
        String authKey = extractAuthKey(exchange);
        if (authKey == null || authKey.isBlank()) {
            return unauthorized(exchange, "Missing authentication key");
        }

        McpAuthKeyInfo authInfo = authService.validateAuthKey(authKey);
        if (authInfo == null) {
            return unauthorized(exchange, "Invalid or expired authentication key");
        }

        // Extract serviceId from path: /mcp/{serviceId}/**
        String serviceId = extractServiceId(path);
        if (serviceId == null) {
            return unauthorized(exchange, "Invalid MCP path: missing service ID");
        }

        // Check service access
        if (!"*".equals(authInfo.getServiceId()) && !serviceId.equals(authInfo.getServiceId())) {
            return unauthorized(exchange, "Access denied for service: " + serviceId);
        }

        // Set exchange attributes for downstream use
        exchange.getAttributes().put(ATTR_AUTH_KEY, authInfo);
        exchange.getAttributes().put(ATTR_SERVICE_ID, serviceId);

        // Update last used time asynchronously
        if (authKey != null) {
            authService.updateLastUsedTime(authKey);
        }

        return chain.filter(exchange);
    }

    private String extractAuthKey(ServerWebExchange exchange) {
        // 1. Query param ?key=
        String queryKey = exchange.getRequest().getQueryParams().getFirst("key");
        if (queryKey != null && !queryKey.isBlank()) {
            return queryKey;
        }

        // 2. X-Api-Key header
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-Api-Key");
        if (apiKey != null && !apiKey.isBlank()) {
            return apiKey;
        }

        // 3. Authorization: Bearer
        String auth = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7).trim();
        }

        return null;
    }

    private String extractServiceId(String path) {
        // /mcp/{serviceId}/... → extract serviceId
        if (!path.startsWith(PATH_PREFIX)) {
            return null;
        }
        String remainder = path.substring(PATH_PREFIX.length());
        int slashIdx = remainder.indexOf('/');
        if (slashIdx > 0) {
            return remainder.substring(0, slashIdx);
        }
        // /mcp/{serviceId} (no trailing path)
        return remainder.isEmpty() ? null : remainder;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "unauthorized");
        body.put("message", message);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = ("{\"error\":\"unauthorized\",\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        }

        var buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
