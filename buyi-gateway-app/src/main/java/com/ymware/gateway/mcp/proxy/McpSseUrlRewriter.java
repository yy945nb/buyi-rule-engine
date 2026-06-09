package com.ymware.gateway.mcp.proxy;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rewrites URLs in SSE data streams.
 * Transforms /mcp/message to /mcp/{serviceId}/message so clients
 * can connect via the gateway path and URLs in SSE events remain valid.
 */
public final class McpSseUrlRewriter {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "(data:)?/mcp/([^?\\s]+)(\\?[^\\s]*)?(\\s|$|\"|')"
    );

    private McpSseUrlRewriter() {}

    public static Flux<DataBuffer> rewriteSseDataBuffers(Flux<DataBuffer> source, ServerWebExchange exchange) {
        String serviceId = extractServiceId(exchange);
        if (serviceId == null) {
            return source;
        }
        return source.map(buffer -> rewriteBuffer(buffer, serviceId));
    }

    private static DataBuffer rewriteBuffer(DataBuffer buffer, String serviceId) {
        byte[] bytes = new byte[buffer.readableByteCount()];
        buffer.read(bytes);
        DataBufferUtils.release(buffer);

        String content = new String(bytes, StandardCharsets.UTF_8);
        String rewritten = rewriteUrlPaths(content, serviceId);
        return DefaultDataBufferFactory.sharedInstance.wrap(rewritten.getBytes(StandardCharsets.UTF_8));
    }

    static String rewriteUrlPaths(String content, String serviceId) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        Matcher matcher = URL_PATTERN.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String prefix = matcher.group(1) != null ? matcher.group(1) : "";
            String path = matcher.group(2);
            String query = matcher.group(3) != null ? matcher.group(3) : "";
            String suffix = matcher.group(4) != null ? matcher.group(4) : "";

            // Only rewrite if path doesn't already contain the serviceId
            if (!path.startsWith(serviceId + "/")) {
                matcher.appendReplacement(sb, prefix + "/mcp/" + serviceId + "/" + path + query + suffix);
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String extractServiceId(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        // /mcp/{serviceId}/...
        if (!path.startsWith("/mcp/")) {
            return null;
        }
        String remainder = path.substring(5);
        int slashIdx = remainder.indexOf('/');
        if (slashIdx > 0) {
            return remainder.substring(0, slashIdx);
        }
        return remainder.isEmpty() ? null : remainder;
    }
}
