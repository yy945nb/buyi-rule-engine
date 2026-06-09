package com.ymware.gateway.mcp.proxy;

import com.ymware.gateway.mcp.discovery.McpServiceDiscovery;
import com.ymware.gateway.mcp.discovery.McpServiceInfo;
import com.ymware.gateway.mcp.protocol.McpProtocolHandler;
import com.ymware.gateway.mcp.routing.RoutingRuleEngine;
import com.ymware.gateway.mcp.routing.model.RouteDecision;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/mcp")
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpProxyController {

    private static final Logger log = LoggerFactory.getLogger(McpProxyController.class);

    private final McpProxyHandler proxyHandler;
    private final McpProtocolHandler protocolHandler;
    private final McpServiceDiscovery serviceDiscovery;
    private final McpProtocolRouter router;
    private final RoutingRuleEngine ruleEngine;
    private final ObjectMapper objectMapper;

    public McpProxyController(McpProxyHandler proxyHandler,
                              McpProtocolHandler protocolHandler,
                              McpServiceDiscovery serviceDiscovery,
                              McpProtocolRouter router,
                              RoutingRuleEngine ruleEngine,
                              ObjectMapper objectMapper) {
        this.proxyHandler = proxyHandler;
        this.protocolHandler = protocolHandler;
        this.serviceDiscovery = serviceDiscovery;
        this.router = router;
        this.ruleEngine = ruleEngine;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/health")
    public Mono<Map<String, String>> health() {
        return Mono.just(Map.of("status", "ok", "service", "mcp-gateway"));
    }

    @RequestMapping(value = "/{serviceId}/**", method = {RequestMethod.GET, RequestMethod.POST,
            RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS})
    public Mono<Void> handleMcpRequest(@PathVariable String serviceId,
                                       ServerWebExchange exchange) {
        McpServiceInfo service = serviceDiscovery.getService(serviceId);
        McpProtocolRouter.McpRouteDecision decision = router.decide(serviceId, service);

        return switch (decision.routeType()) {
            case NOT_FOUND -> writeError(exchange, HttpStatus.NOT_FOUND, decision.errorMessage());
            case INACTIVE -> writeError(exchange, HttpStatus.SERVICE_UNAVAILABLE, decision.errorMessage());
            case TRANSPARENT -> proxyHandler.proxyRequest(exchange, service, extractRemainingPath(exchange, serviceId));
            case PROTOCOL_PARSE -> handleProtocolParse(serviceId, service, exchange);
        };
    }

    private Mono<Void> handleProtocolParse(String serviceId, McpServiceInfo service, ServerWebExchange exchange) {
        if (exchange.getRequest().getMethod() != org.springframework.http.HttpMethod.POST) {
            return writeError(exchange, HttpStatus.METHOD_NOT_ALLOWED,
                    "Protocol parse mode only supports POST requests");
        }

        return exchange.getRequest().getBody()
                .map(db -> {
                    byte[] bytes = new byte[db.readableByteCount()];
                    db.read(bytes);
                    org.springframework.core.io.buffer.DataBufferUtils.release(db);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .reduce(new StringBuilder(), StringBuilder::append)
                .map(StringBuilder::toString)
                .flatMap(body -> {
                    // 应用路由规则引擎：检查是否需要重定向到其他服务
                    String resolvedServiceId = applyRoutingRules(serviceId, body);

                    Flux<ServerSentEvent<String>> events = protocolHandler.handleRequest(
                            resolvedServiceId, body, exchange);
                    return writeSseResponse(exchange, events);
                });
    }

    /**
     * 应用路由规则引擎，决定最终路由到哪个服务。
     * 从请求体中提取工具名，匹配规则，返回目标 serviceId。
     */
    private String applyRoutingRules(String defaultServiceId, String rawBody) {
        try {
            String toolName = extractToolName(rawBody);
            String serviceType = extractServiceType(defaultServiceId);

            RouteDecision routeDecision = ruleEngine.route(toolName, serviceType, null, rawBody);

            switch (routeDecision.getType()) {
                case RULE_MATCHED:
                    String target = routeDecision.getTargetServiceId();
                    if ("*".equals(target)) {
                        return defaultServiceId;
                    }
                    log.debug("Routing rule '{}' matched: {} -> {}",
                            routeDecision.getMatchedRuleName(), toolName, target);
                    return target;
                case FALLBACK_TO_DEFAULT:
                    log.warn("Routing rule fallback: {}", routeDecision.getReason());
                    return defaultServiceId;
                case DENIED:
                    log.warn("Routing rule denied: {}", routeDecision.getReason());
                    return defaultServiceId;
                default:
                    return defaultServiceId;
            }
        } catch (Exception e) {
            log.debug("Routing rule evaluation failed, using default: {}", e.getMessage());
            return defaultServiceId;
        }
    }

    private String extractToolName(String rawBody) {
        try {
            JsonNode root = objectMapper.readTree(rawBody);
            String method = root.has("method") ? root.get("method").asText() : null;
            if ("tools/call".equals(method) && root.has("params")) {
                JsonNode params = root.get("params");
                return params.has("name") ? params.get("name").asText() : null;
            }
            return method;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractServiceType(String serviceId) {
        McpServiceInfo service = serviceDiscovery.getService(serviceId);
        return service != null && service.getServiceType() != null
                ? service.getServiceType().name() : null;
    }

    private Mono<Void> writeSseResponse(ServerWebExchange exchange, Flux<ServerSentEvent<String>> events) {
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);
        exchange.getResponse().getHeaders().set("Cache-Control", "no-cache");
        exchange.getResponse().getHeaders().set("Connection", "keep-alive");

        return exchange.getResponse().writeWith(
                events.map(event -> {
                    String data = "event: " + event.event() + "\ndata: " + event.data() + "\n\n";
                    return exchange.getResponse().bufferFactory().wrap(data.getBytes(StandardCharsets.UTF_8));
                })
        );
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"error\":\"" + message + "\"}";
        var buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private String extractRemainingPath(ServerWebExchange exchange, String serviceId) {
        String path = exchange.getRequest().getURI().getPath();
        String prefix = "/mcp/" + serviceId;
        if (path.startsWith(prefix)) {
            String remaining = path.substring(prefix.length());
            return remaining.isEmpty() ? "/" : remaining;
        }
        return "/";
    }
}
