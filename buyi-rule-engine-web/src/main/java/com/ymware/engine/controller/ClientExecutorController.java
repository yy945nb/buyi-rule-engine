package com.ymware.engine.controller;

import com.ymware.engine.model.request.ConfigDiscoverRequest;
import com.ymware.engine.result.ExpressionConfigInfo;
import com.ymware.engine.model.dto.ExpressionExecutorResultDTO;
import com.ymware.engine.model.GlobalExpressionDocInfo;
import com.ymware.engine.consts.ExpressionConstants;
import com.ymware.engine.exception.Throws;
import com.ymware.engine.service.ExpressionExecutorService;
import com.ymware.engine.model.response.RestResult;
import com.ymware.engine.service.ExpressionKeyService;
import com.ymware.engine.service.ExpressionTraceLogIndexService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 远端节点提交执行器
 */
@Tag(name = "远端节点提交执行器")
@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping
public class ClientExecutorController {
    @Autowired
    private ExpressionExecutorService expressionExecutor;
    @Autowired
    private ExpressionTraceLogIndexService traceService;
    @Autowired
    private ExpressionKeyService expressionKeyService;

    @Operation(summary = "函数变量doc提交")
    @PostMapping(ExpressionConstants.SERVER_DOC_SUBMIT)
    public RestResult<Object> varFnDocSubmit(@RequestBody @Validated GlobalExpressionDocInfo docInfo) {
        CompletableFuture.runAsync(() -> expressionKeyService.refreshDocument(docInfo));
        return RestResult.ok("ok");
    }

    /**
     * 客户端获取远端的表达式配置，然后交由客户端自行执行
     *
     * @param baseRequest
     * @return
     */
    @Operation(summary = "远端表达式配置发现")
    @PostMapping(ExpressionConstants.SERVER_CONFIG_DISCOVERY)
    public RestResult<Object> configDiscovery(@RequestBody @Validated ConfigDiscoverRequest baseRequest) {
        Throws.nullError(baseRequest.getServiceName(), "serviceName");
        Throws.nullError(baseRequest.getBusinessCode(), "businessCode");
        ExpressionConfigInfo businessConfigInfo = expressionExecutor.queryBusinessConfigInfo(baseRequest.getServiceName(), baseRequest.getBusinessCode(), baseRequest.getExecutorCode());
        return RestResult.ok(businessConfigInfo);
    }

    @Operation(summary = "提交客户端追踪日志")
    @PostMapping(ExpressionConstants.SERVER_EXECUTOR_TRACE_SUBMIT_PATH)
    public RestResult<Object> traceLogSubmit(@RequestBody @Validated List<ExpressionExecutorResultDTO> request) {
        boolean result = traceService.addTraceLog(request);
        return RestResult.ok(result);
    }

}
