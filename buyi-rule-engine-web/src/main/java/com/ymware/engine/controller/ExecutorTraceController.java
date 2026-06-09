package com.ymware.engine.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ymware.engine.constants.BaseConstants;
import com.ymware.engine.entity.ExpressionTraceLogIndex;
import com.ymware.engine.model.request.QueryExpressionTraceRequest;
import com.ymware.engine.model.dto.response.ExpressionTraceInfoDTO;
import com.ymware.engine.model.response.RestResult;
import com.ymware.engine.service.ExpressionTraceLogIndexService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 */
@Tag(name = "执行器表达式追踪")
@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(BaseConstants.BASE_PATH + "/executor/trace")
public class ExecutorTraceController {
    @Autowired
    private ExpressionTraceLogIndexService traceLogIndexService;

    @Operation(summary = "查询追踪日志列表")
    @PostMapping("/list")
    public RestResult<Page<ExpressionTraceLogIndex>> findExecutorList(@RequestBody QueryExpressionTraceRequest queryRequest) {
        Page<ExpressionTraceLogIndex> page = traceLogIndexService.queryExpressionTraceLogList(queryRequest);
        return RestResult.ok(page);
    }

    @Operation(summary = "查询单个追踪链路")
    @PostMapping("/info")
    public RestResult<ExpressionTraceInfoDTO> findExecutorList(@RequestParam("id") Long id) {
        final ExpressionTraceInfoDTO info = traceLogIndexService.getTraceInfoList(id);
        return RestResult.ok(info);
    }

    @Operation(summary = "获取表达式样本参数")
    @PostMapping("/getExpressionSampleBody")
    public RestResult<ExpressionTraceLogIndex> getExpressionSampleBody(@RequestParam("expressionId") Long expressionId) {
        ExpressionTraceLogIndex logIndex = traceLogIndexService.getExpressionSampleBody(expressionId);
        return RestResult.ok(logIndex);
    }

}
