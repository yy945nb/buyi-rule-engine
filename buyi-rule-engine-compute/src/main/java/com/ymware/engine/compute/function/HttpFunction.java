package com.ymware.engine.compute.function;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.ymware.engine.annotation.Executor;
import com.ymware.engine.annotation.Function;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * HTTP函数 - 支持SSRF防护、超时和资源释放。
 */
@Slf4j
@Function
public class HttpFunction {

    private static final int DEFAULT_TIMEOUT_MS = 10000;
    private static final int MAX_TIMEOUT_MS = 60000;

    private static final String[] BLOCKED_HOSTS = {
            "localhost", "metadata", "169.254.169.254"
    };

    @Executor
    public Object executor(@Valid Params params) {
        String method = params.getRequestMethod();
        String requestUrl = params.getRequestUrl();
        String requestBody = params.getRequestBody();

        validateUrl(requestUrl);

        int timeout = params.getTimeout() != null ? params.getTimeout() : DEFAULT_TIMEOUT_MS;
        timeout = Math.min(timeout, MAX_TIMEOUT_MS);

        HttpResponse execute = null;
        try {
            if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
                JSONObject jsonObject = JSON.parseObject(requestBody);
                execute = HttpUtil.createGet(requestUrl).form(jsonObject).timeout(timeout).execute();
            } else {
                execute = HttpUtil.createPost(requestUrl).body(requestBody).timeout(timeout).execute();
            }
            String body = execute.body();
            log.info("HTTP请求响应: url={}, status={}", requestUrl, execute.getStatus());
            return body;
        } finally {
            if (execute != null) {
                try {
                    execute.close();
                } catch (Exception e) {
                    log.debug("关闭HttpResponse异常", e);
                }
            }
        }
    }

    private void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("requestUrl不能为空");
        }
        String lower = url.toLowerCase().trim();
        if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
            throw new SecurityException("仅支持http/https协议");
        }
        try {
            URI uri = URI.create(lower);
            String host = uri.getHost();
            if (host == null) {
                throw new SecurityException("无法解析URL主机名: " + url);
            }
            for (String blocked : BLOCKED_HOSTS) {
                if (host.equals(blocked)) {
                    throw new SecurityException("禁止访问内网地址: " + url);
                }
            }
            InetAddress addr = InetAddress.getByName(host);
            if (addr.isLoopbackAddress() || addr.isSiteLocalAddress() || addr.isLinkLocalAddress()
                    || addr.isAnyLocalAddress() || addr.getHostAddress().startsWith("fd")) {
                throw new SecurityException("禁止访问内网地址: " + url + " (resolved: " + addr.getHostAddress() + ")");
            }
        } catch (UnknownHostException e) {
            throw new SecurityException("无法解析主机名: " + url);
        }
    }

    @Data
    public static class Params {

        @NotBlank
        private String requestUrl;

        @NotBlank
        private String requestMethod;

        /** 请求body */
        private String requestBody;

        /** 超时时间(ms)，默认10000，最大60000 */
        private Integer timeout;
    }

}
