package com.ymware.gateway.mcp.controller;

import com.ymware.gateway.common.result.PageResult;
import com.ymware.gateway.common.result.R;
import com.ymware.gateway.mcp.discovery.McpServiceInfo;
import com.ymware.gateway.mcp.mapper.McpServiceMapper;
import com.ymware.gateway.mcp.model.McpServiceDO;
import com.ymware.gateway.mcp.service.McpHealthCheckService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/mcp/services")
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpServiceAdminController {

    private final McpServiceMapper serviceMapper;
    private final McpHealthCheckService healthCheckService;

    public McpServiceAdminController(McpServiceMapper serviceMapper, McpHealthCheckService healthCheckService) {
        this.serviceMapper = serviceMapper;
        this.healthCheckService = healthCheckService;
    }

    @PostMapping("/add")
    public Mono<R<Void>> add(@RequestBody McpServiceDO record) {
        return Mono.fromRunnable(() -> serviceMapper.insert(record))
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @PostMapping("/update")
    public Mono<R<Void>> update(@RequestBody McpServiceDO record) {
        return Mono.fromRunnable(() -> serviceMapper.update(record))
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @PostMapping("/delete/{id}")
    public Mono<R<Void>> delete(@PathVariable Long id) {
        return Mono.fromRunnable(() -> serviceMapper.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @GetMapping("/{id}")
    public Mono<R<McpServiceDO>> getById(@PathVariable Long id) {
        return Mono.fromCallable(() -> serviceMapper.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @PostMapping("/list")
    public Mono<R<PageResult<McpServiceDO>>> list(@RequestBody Map<String, Object> req) {
        return Mono.fromCallable(() -> {
            String status = (String) req.get("status");
            String name = (String) req.get("name");
            int page = req.get("page") != null ? (int) req.get("page") : 1;
            int size = req.get("size") != null ? (int) req.get("size") : 20;
            int offset = (page - 1) * size;

            List<McpServiceDO> list = serviceMapper.findByConditions(status, name, offset, size);
            int total = serviceMapper.countByConditions(status, name);
            return new PageResult<>(list, total, page, size);
        }).subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @PostMapping("/{serviceId}/health-check")
    public Mono<R<Map<String, Object>>> healthCheck(@PathVariable String serviceId) {
        return Mono.fromCallable(() -> {
            McpServiceDO record = serviceMapper.findByServiceId(serviceId);
            if (record == null) {
                return Map.<String, Object>of("healthy", false, "error", "Service not found");
            }
            McpServiceInfo info = McpServiceInfo.builder()
                    .serviceId(record.getServiceId())
                    .endpoint(record.getEndpoint())
                    .healthCheckUrl(record.getHealthCheckUrl())
                    .build();
            boolean healthy = healthCheckService.checkService(info);
            return Map.<String, Object>of("healthy", healthy, "serviceId", serviceId);
        }).subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }
}
