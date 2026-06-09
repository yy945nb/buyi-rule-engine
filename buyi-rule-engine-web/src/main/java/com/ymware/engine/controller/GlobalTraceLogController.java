package com.ymware.engine.controller;


import com.ymware.engine.constants.BaseConstants;
import com.ymware.engine.model.dto.request.AddGlobalTraceLogRequest;
import com.ymware.engine.model.dto.request.DeleteByIdListRequest;
import com.ymware.engine.model.dto.request.EditGlobalTraceLogRequest;
import com.ymware.engine.model.request.QueryGlobalTraceLogRequest;
import com.ymware.engine.model.dto.response.ExpressionGlobalTraceLogDTO;
import com.ymware.engine.model.response.RestResult;
import com.ymware.engine.service.GlobalTraceLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 全局日志表,负责记录全局日志执行过程的日志记录,负责排查执行过程 前端控制器
 * </p>
 *
 * @author bsy
 * @since 2022-06-15
 */
@Tag(name = "全局日志管理")
@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(BaseConstants.BASE_PATH + "/log/trace")
public class GlobalTraceLogController {
    @Autowired
    private GlobalTraceLogService expressionGlobalTraceLogService;

    @Operation(summary = "添加单个全局日志")
    @PostMapping("/addOne")
    public RestResult<ExpressionGlobalTraceLogDTO> addOne(@RequestBody AddGlobalTraceLogRequest addRequest) {
        return expressionGlobalTraceLogService.addOne(addRequest);
    }

    @Operation(summary = "编辑单个全局日志")
    @PostMapping("/editOne")
    public RestResult<ExpressionGlobalTraceLogDTO> editOne(@RequestBody EditGlobalTraceLogRequest editRequest) {
        return expressionGlobalTraceLogService.updateOne(editRequest);
    }

    @Operation(summary = "查询全局日志")
    @PostMapping("/findGlobalTraceLogList")
    public RestResult<List<ExpressionGlobalTraceLogDTO>> findExpressionList(@RequestBody QueryGlobalTraceLogRequest queryRequest) {
        return expressionGlobalTraceLogService.queryDtoList(queryRequest);
    }

    @Operation(summary = "批量逻辑删除全局日志")
    @PostMapping("/batchDelete")
    public RestResult<?> batchDelete(@RequestBody DeleteByIdListRequest delRequest) {
        return expressionGlobalTraceLogService.logicDeleteByIdList(delRequest);
    }
}
