package com.ymware.gateway.mcp.proxy;

import com.ymware.gateway.mcp.config.McpProperties;
import com.ymware.gateway.mcp.discovery.McpServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpProxyHandler {

    private static final Logger log = LoggerFactory.getLogger(McpProxyHandler.class);
    private static final List<String> HOP_BY_HOP_HEADERS = List.of(
            "connection", "keep-alive", "transfer-encoding", "te", "trailer",
            "proxy-authorization", "proxy-authenticate", "upgrade"
    );

    private final WebClient webClient;

    public McpProxyHandler(ReactorClientHttpConnector connector, McpProperties properties) {
        this.webClient = WebClient.builder()
                .clientConnector(connector)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(properties.getProxy().getMaxInMemorySize()))
                .build();
    }

    public Mono<Void> proxyRequest(ServerWebExchange exchange, McpServiceInfo service, String remainingPath) {
        String targetUrl = buildTargetUrl(service.getEndpoint(), remainingPath, exchange);
        HttpMethod method = exchange.getRequest().getMethod();

        log.debug("Proxying {} {} -> {}", method, exchange.getRequest().getURI(), targetUrl);

        WebClient.RequestBodySpec requestSpec = webClient.method(method)
                .uri(targetUrl)
                .headers(headers -> {
                    // Copy headers, skip hop-by-hop
                    exchange.getRequest().getHeaders().forEach((key, values) -> {
                        if (!HOP_BY_HOP_HEADERS.contains(key.toLowerCase()) && !key.equalsIgnoreCase("host")) {
                            headers.put(key, values);
                        }
                    });
                    headers.set("X-Forwarded-For", getClientIp(exchange));
                });

        boolean isSse = isSseRequest(exchange);

        if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
            Flux<DataBuffer> body = exchange.getRequest().getBody();
            return requestSpec
                    .contentType(exchange.getRequest().getHeaders().getContentType())
                    .body(body, DataBuffer.class)
                    .exchangeToMono(response -> {
                        if (isSse) {
                            return proxySseResponse(exchange, response);
                        }
                        return proxyNormalResponse(exchange, response);
                    })
                    .doOnError(e -> log.error("Proxy error for {}: {}", targetUrl, e.getMessage()));
        } else {
            return requestSpec
                    .exchangeToMono(response -> {
                        if (isSse) {
                            return proxySseResponse(exchange, response);
                        }
                        return proxyNormalResponse(exchange, response);
                    })
                    .doOnError(e -> log.error("Proxy error for {}: {}", targetUrl, e.getMessage()));
        }
    }

    private Mono<Void> proxySseResponse(ServerWebExchange exchange, org.springframework.web.reactive.function.client.ClientResponse response) {
        exchange.getResponse().setStatusCode(response.statusCode());
        exchange.getResponse().getHeaders().set("Content-Type", "text/event-stream");
        exchange.getResponse().getHeaders().set("Cache-Control", "no-cache");
        exchange.getResponse().getHeaders().set("Connection", "keep-alive");

        Flux<DataBuffer> responseBody = response.bodyToFlux(DataBuffer.class);

        // Apply URL rewriting to SSE data
        Flux<DataBuffer> rewrittenBody = McpSseUrlRewriter.rewriteSseDataBuffers(responseBody, exchange);

        return exchange.getResponse().writeWith(rewrittenBody)
                .doOnError(e -> log.error("SSE proxy write error: {}", e.getMessage()));
    }

    private Mono<Void> proxyNormalResponse(ServerWebExchange exchange, org.springframework.web.reactive.function.client.ClientResponse response) {
        exchange.getResponse().setStatusCode(response.statusCode());
        response.headers().asHttpHeaders().forEach((key, values) -> {
            if (!HOP_BY_HOP_HEADERS.contains(key.toLowerCase())) {
                exchange.getResponse().getHeaders().put(key, values);
            }
        });

        Flux<DataBuffer> responseBody = response.bodyToFlux(DataBuffer.class);
        return exchange.getResponse().writeWith(responseBody);
    }

    private String buildTargetUrl(String endpoint, String remainingPath, ServerWebExchange exchange) {
        String base = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        String path = remainingPath.startsWith("/") ? remainingPath : "/" + remainingPath;
        String query = exchange.getRequest().getURI().getQuery();
        if (query != null && !query.isEmpty()) {
            return base + path + "?" + query;
        }
        return base + path;
    }

    private boolean isSseRequest(ServerWebExchange exchange) {
        String accept = exchange.getRequest().getHeaders().getFirst(HttpHeaders.ACCEPT);
        return accept != null && accept.contains("text/event-stream");
    }

    private String getClientIp(ServerWebExchange exchange) {
        String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0].trim();
        }
        var addr = exchange.getRequest().getRemoteAddress();
        return addr != null ? addr.getAddress().getHostAddress() : "unknown";
    }
}
