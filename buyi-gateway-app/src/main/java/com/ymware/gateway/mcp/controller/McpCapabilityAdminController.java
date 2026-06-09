package com.ymware.gateway.mcp.controller;

import com.ymware.gateway.common.result.R;
import com.ymware.gateway.mcp.routing.CapabilityRegistry;
import com.ymware.gateway.mcp.routing.mapper.ServiceCapabilityMapper;
import com.ymware.gateway.mcp.routing.model.ServiceCapabilityDO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/admin/mcp/capabilities")
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpCapabilityAdminController {

    private final ServiceCapabilityMapper capabilityMapper;
    private final CapabilityRegistry capabilityRegistry;

    public McpCapabilityAdminController(ServiceCapabilityMapper capabilityMapper,
                                        CapabilityRegistry capabilityRegistry) {
        this.capabilityMapper = capabilityMapper;
        this.capabilityRegistry = capabilityRegistry;
    }

    @PostMapping("/add")
    public Mono<R<Void>> add(@RequestBody ServiceCapabilityDO record) {
        return Mono.fromRunnable(() -> capabilityRegistry.register(record))
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @PostMapping("/update")
    public Mono<R<Void>> update(@RequestBody ServiceCapabilityDO record) {
        return Mono.fromRunnable(() -> {
            capabilityMapper.update(record);
            capabilityRegistry.refresh();
        }).subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @PostMapping("/delete/{id}")
    public Mono<R<Void>> delete(@PathVariable Long id) {
        return Mono.fromRunnable(() -> {
            capabilityMapper.deleteById(id);
            capabilityRegistry.refresh();
        }).subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @GetMapping("/service/{serviceId}")
    public Mono<R<List<ServiceCapabilityDO>>> getByService(@PathVariable String serviceId) {
        return Mono.fromCallable(() -> capabilityMapper.findByServiceId(serviceId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @GetMapping("/tag/{capabilityTag}")
    public Mono<R<List<ServiceCapabilityDO>>> getByTag(@PathVariable String capabilityTag) {
        return Mono.fromCallable(() -> capabilityRegistry.getHealthyServices(capabilityTag))
                .subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @GetMapping("/service/{serviceId}/tags")
    public Mono<R<Set<String>>> getServiceTags(@PathVariable String serviceId) {
        return Mono.fromCallable(() -> capabilityRegistry.getCapabilities(serviceId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @PostMapping("/list")
    public Mono<R<List<ServiceCapabilityDO>>> listAll() {
        return Mono.fromCallable(() -> capabilityMapper.findAll())
                .subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @PostMapping("/{serviceId}/{tag}/health")
    public Mono<R<Void>> updateHealth(@PathVariable String serviceId,
                                      @PathVariable String tag,
                                      @RequestBody Map<String, Boolean> req) {
        return Mono.fromRunnable(() -> {
            Boolean healthy = req.get("healthy");
            capabilityRegistry.updateHealth(serviceId, tag, Boolean.TRUE.equals(healthy));
        }).subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @PostMapping("/refresh")
    public Mono<R<Void>> refresh() {
        return Mono.fromRunnable(() -> capabilityRegistry.refresh())
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }
}
