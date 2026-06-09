package com.ymware.gateway.mcp.controller;

import com.ymware.gateway.common.result.PageResult;
import com.ymware.gateway.common.result.R;
import com.ymware.gateway.mcp.mapper.McpAuthKeyMapper;
import com.ymware.gateway.mcp.model.McpAuthKeyDO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/mcp/auth-keys")
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpAuthKeyAdminController {

    private final McpAuthKeyMapper authKeyMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    public McpAuthKeyAdminController(McpAuthKeyMapper authKeyMapper) {
        this.authKeyMapper = authKeyMapper;
    }

    @PostMapping("/apply")
    public Mono<R<Map<String, String>>> apply(@RequestBody Map<String, String> req) {
        return Mono.fromCallable(() -> {
            String userId = req.get("userId");
            String serviceId = req.get("serviceId");
            if (userId == null || serviceId == null) {
                throw new IllegalArgumentException("userId and serviceId are required");
            }

            // Generate raw key
            String rawKey = generateKey();
            String keyHash = hashKey(rawKey);
            String keyPrefix = rawKey.substring(0, 8);

            McpAuthKeyDO record = new McpAuthKeyDO();
            record.setKeyHash(keyHash);
            record.setKeyPrefix(keyPrefix);
            record.setUserId(userId);
            record.setServiceId(serviceId);
            record.setIsActive(true);
            authKeyMapper.insert(record);

            return Map.of("key", rawKey, "keyPrefix", keyPrefix, "id", record.getId().toString());
        }).subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @PostMapping("/batch-apply")
    public Mono<R<List<Map<String, String>>>> batchApply(@RequestBody Map<String, Object> req) {
        return Mono.fromCallable(() -> {
            String userId = (String) req.get("userId");
            List<String> serviceIds = (List<String>) req.get("serviceIds");
            if (userId == null || serviceIds == null) {
                throw new IllegalArgumentException("userId and serviceIds are required");
            }

            return serviceIds.stream().map(serviceId -> {
                String rawKey = generateKey();
                String keyHash = hashKey(rawKey);
                String keyPrefix = rawKey.substring(0, 8);

                McpAuthKeyDO record = new McpAuthKeyDO();
                record.setKeyHash(keyHash);
                record.setKeyPrefix(keyPrefix);
                record.setUserId(userId);
                record.setServiceId(serviceId);
                record.setIsActive(true);
                authKeyMapper.insert(record);

                return Map.of("key", rawKey, "keyPrefix", keyPrefix, "serviceId", serviceId);
            }).toList();
        }).subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @GetMapping("/user/{userId}")
    public Mono<R<List<McpAuthKeyDO>>> getByUser(@PathVariable String userId) {
        return Mono.fromCallable(() -> authKeyMapper.findByUserId(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @PostMapping("/list")
    public Mono<R<PageResult<McpAuthKeyDO>>> list(@RequestBody Map<String, Object> req) {
        return Mono.fromCallable(() -> {
            String userId = (String) req.get("userId");
            String serviceId = (String) req.get("serviceId");
            int page = req.get("page") != null ? (int) req.get("page") : 1;
            int size = req.get("size") != null ? (int) req.get("size") : 20;
            int offset = (page - 1) * size;

            List<McpAuthKeyDO> list = authKeyMapper.findByConditions(userId, serviceId, offset, size);
            int total = authKeyMapper.countByConditions(userId, serviceId);
            return new PageResult<>(list, total, page, size);
        }).subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @PostMapping("/delete/{keyId}")
    public Mono<R<Void>> delete(@PathVariable Long keyId) {
        return Mono.fromRunnable(() -> authKeyMapper.deleteById(keyId))
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @PostMapping("/{keyId}/renew")
    public Mono<R<Void>> renew(@PathVariable Long keyId, @RequestBody Map<String, Object> req) {
        return Mono.fromRunnable(() -> {
            McpAuthKeyDO record = authKeyMapper.findById(keyId);
            if (record == null) {
                throw new IllegalArgumentException("Key not found");
            }
            String expiresAtStr = (String) req.get("expiresAt");
            if (expiresAtStr != null) {
                record.setExpiresAt(LocalDateTime.parse(expiresAtStr));
            }
            record.setIsActive(true);
            authKeyMapper.update(record);
        }).subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    private String generateKey() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return "mcp-" + HexFormat.of().formatHex(bytes);
    }

    private String hashKey(String rawKey) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
