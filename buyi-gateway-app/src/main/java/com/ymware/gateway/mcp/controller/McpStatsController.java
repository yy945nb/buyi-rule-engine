package com.ymware.gateway.mcp.controller;

import com.ymware.gateway.common.result.R;
import com.ymware.gateway.mcp.mapper.McpServiceStatsMapper;
import com.ymware.gateway.mcp.model.McpServiceStatsDO;
import com.ymware.gateway.mcp.stats.McpServiceStats;
import com.ymware.gateway.mcp.stats.McpStatsCollector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/mcp/stats")
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpStatsController {

    private final McpStatsCollector statsCollector;
    private final McpServiceStatsMapper statsMapper;

    public McpStatsController(McpStatsCollector statsCollector, McpServiceStatsMapper statsMapper) {
        this.statsCollector = statsCollector;
        this.statsMapper = statsMapper;
    }

    @GetMapping("/{serviceId}/realtime")
    public Mono<R<McpServiceStats>> getRealtimeStats(@PathVariable String serviceId) {
        return Mono.fromCallable(() -> statsCollector.getRealtimeStats(serviceId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @GetMapping("/{serviceId}")
    public Mono<R<List<McpServiceStatsDO>>> getHistoricalStats(
            @PathVariable String serviceId,
            @RequestParam(defaultValue = "30") int days) {
        return Mono.fromCallable(() -> statsMapper.findRecentByServiceId(serviceId, days))
                .subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @PostMapping("/flush")
    public Mono<R<Void>> manualFlush() {
        return Mono.fromRunnable(() -> statsCollector.flushToDatabase())
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }
}
