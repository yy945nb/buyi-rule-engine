package com.ymware.gateway.mcp.controller;

import com.ymware.gateway.common.result.PageResult;
import com.ymware.gateway.common.result.R;
import com.ymware.gateway.mcp.routing.RoutingRuleEngine;
import com.ymware.gateway.mcp.routing.mapper.RoutingRuleMapper;
import com.ymware.gateway.mcp.routing.model.RouteDecision;
import com.ymware.gateway.mcp.routing.model.RoutingRuleDO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/mcp/routing-rules")
@ConditionalOnProperty(prefix = "gateway.mcp", name = "enabled", havingValue = "true")
public class McpRoutingRuleAdminController {

    private final RoutingRuleMapper ruleMapper;
    private final RoutingRuleEngine ruleEngine;

    public McpRoutingRuleAdminController(RoutingRuleMapper ruleMapper, RoutingRuleEngine ruleEngine) {
        this.ruleMapper = ruleMapper;
        this.ruleEngine = ruleEngine;
    }

    @PostMapping("/add")
    public Mono<R<Void>> add(@RequestBody RoutingRuleDO record) {
        return Mono.fromRunnable(() -> ruleMapper.insert(record))
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @PostMapping("/update")
    public Mono<R<Void>> update(@RequestBody RoutingRuleDO record) {
        return Mono.fromRunnable(() -> ruleMapper.update(record))
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @PostMapping("/delete/{id}")
    public Mono<R<Void>> delete(@PathVariable Long id) {
        return Mono.fromRunnable(() -> ruleMapper.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }

    @GetMapping("/{id}")
    public Mono<R<RoutingRuleDO>> getById(@PathVariable Long id) {
        return Mono.fromCallable(() -> ruleMapper.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    @PostMapping("/list")
    public Mono<R<PageResult<RoutingRuleDO>>> list(@RequestBody Map<String, Object> req) {
        return Mono.fromCallable(() -> {
            Boolean enabled = req.get("enabled") != null ? (Boolean) req.get("enabled") : null;
            String keyword = (String) req.get("keyword");
            int page = req.get("page") != null ? (int) req.get("page") : 1;
            int size = req.get("size") != null ? (int) req.get("size") : 20;
            int offset = (page - 1) * size;

            List<RoutingRuleDO> list = ruleMapper.findByConditions(enabled, keyword, offset, size);
            int total = ruleMapper.countByConditions(enabled, keyword);
            return new PageResult<>(list, total, page, size);
        }).subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    /**
     * 测试规则匹配：输入工具名，返回匹配的规则和路由决策。
     */
    @PostMapping("/test")
    public Mono<R<Map<String, Object>>> testMatch(@RequestBody Map<String, String> req) {
        return Mono.fromCallable(() -> {
            String toolName = req.get("toolName");
            String serviceType = req.get("serviceType");

            RouteDecision decision = ruleEngine.route(toolName, serviceType, null, null);

            return Map.<String, Object>of(
                    "toolName", toolName != null ? toolName : "",
                    "decision", decision.getType().name(),
                    "matchedRule", decision.getMatchedRuleName() != null ? decision.getMatchedRuleName() : "",
                    "targetServiceId", decision.getTargetServiceId() != null ? decision.getTargetServiceId() : "",
                    "reason", decision.getReason() != null ? decision.getReason() : ""
            );
        }).subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }
}
