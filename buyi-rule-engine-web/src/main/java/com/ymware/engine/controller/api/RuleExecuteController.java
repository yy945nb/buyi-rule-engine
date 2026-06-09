package com.ymware.engine.controller.api;

import com.ymware.engine.domain.rule.service.ExecutionResult;
import com.ymware.engine.domain.rule.service.RuleEngineService;
import com.ymware.engine.model.response.RestResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 规则执行API - 对外暴露规则引擎执行能力
 */
@Slf4j
@Tag(name = "规则执行")
@RestController
@RequestMapping("api/rule")
public class RuleExecuteController {

    @Resource
    private RuleEngineService ruleEngineService;

    /**
     * 执行已发布的规则流程
     *
     * @param flowCode  流程编码
     * @param variables 输入变量
     * @return 执行结果（包含所有节点输出变量）
     */
    @Operation(summary = "执行规则流程")
    @PostMapping("execute/{flowCode}")
    public RestResult<Map<String, Object>> executeFlow(
            @PathVariable String flowCode,
            @RequestBody(required = false) Map<String, Object> variables) {
        try {
            ExecutionResult result = ruleEngineService.executeFlow(flowCode, variables);

            Map<String, Object> data = new HashMap<>();
            data.put("success", result.isSuccess());
            data.put("executionTimeMs", result.getExecutionTimeMs());
            if (result.isSuccess() && result.getContext() != null) {
                data.put("variables", result.getContext().snapshotVariables());
            }
            if (result.isFailure()) {
                data.put("error", result.getErrorMessage());
            }

            return result.isSuccess() ? RestResult.ok(data) : RestResult.failed(500, result.getErrorMessage(), data);
        } catch (IllegalArgumentException e) {
            return RestResult.failed(404, e.getMessage());
        } catch (Exception e) {
            log.error("Rule execution failed for flow: {}", flowCode, e);
            return RestResult.failed(500, "执行失败: " + e.getMessage());
        }
    }

    /**
     * 使用默认规则引擎执行（无需指定流程编码）
     *
     * @param variables 输入变量
     * @return 执行结果
     */
    @Operation(summary = "执行默认规则")
    @PostMapping("execute")
    public RestResult<Map<String, Object>> executeDefault(@RequestBody Map<String, Object> variables) {
        try {
            ExecutionResult result = ruleEngineService.execute(variables);

            Map<String, Object> data = new HashMap<>();
            data.put("success", result.isSuccess());
            data.put("executionTimeMs", result.getExecutionTimeMs());
            if (result.isSuccess() && result.getContext() != null) {
                data.put("variables", result.getContext().snapshotVariables());
            }
            if (result.isFailure()) {
                data.put("error", result.getErrorMessage());
            }

            return result.isSuccess() ? RestResult.ok(data) : RestResult.failed(500, result.getErrorMessage(), data);
        } catch (Exception e) {
            log.error("Default rule execution failed", e);
            return RestResult.failed(500, "执行失败: " + e.getMessage());
        }
    }
}
