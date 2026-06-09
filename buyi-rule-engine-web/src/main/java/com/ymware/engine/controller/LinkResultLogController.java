package com.ymware.engine.controller;


import com.ymware.engine.constants.BaseConstants;
import com.ymware.engine.model.dto.request.AddLinkResultLogRequest;
import com.ymware.engine.model.dto.request.DeleteByIdListRequest;
import com.ymware.engine.model.request.EditLinkResultLogRequest;
import com.ymware.engine.model.dto.request.QueryLinkResultLogRequest;
import com.ymware.engine.model.dto.response.ExpressionLinkResultLogDTO;
import com.ymware.engine.model.response.RestResult;
import com.ymware.engine.service.ExpressionLinkResultLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 成功回调日志表,记录执行完成的日志记录,成功回调日志表的压缩版 前端控制器
 * </p>
 *
 * @author bsy
 * @since 2022-06-15
 */
@Tag(name = "成功调度日志管理")
@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(BaseConstants.BASE_PATH + "/log/success")
@Deprecated
public class LinkResultLogController {
    @Autowired
    private ExpressionLinkResultLogService expressionLinkResultLogService;

    @Operation(summary = "添加单个成功回调日志")
    @PostMapping("/addOne")
    public RestResult<ExpressionLinkResultLogDTO> addOne(@Validated @RequestBody AddLinkResultLogRequest addRequest) {
        return expressionLinkResultLogService.addOne(addRequest);
    }

    @Operation(summary = "编辑单个成功回调日志")
    @PostMapping("/editOne")
    public RestResult<ExpressionLinkResultLogDTO> editOne(@RequestBody @Validated EditLinkResultLogRequest editRequest) {
        return expressionLinkResultLogService.updateOne(editRequest);
    }

    @Operation(summary = "查询成功回调日志")
    @PostMapping("/findLinkResultLogList")
    public RestResult<List<ExpressionLinkResultLogDTO>> findExpressionList(@RequestBody QueryLinkResultLogRequest queryRequest) {
        return expressionLinkResultLogService.queryDtoList(queryRequest);
    }

    @Operation(summary = "批量逻辑删除成功回调日志")
    @PostMapping("/batchDelete")
    public RestResult<?> batchDelete(@RequestBody @Validated DeleteByIdListRequest delRequest) {
        return expressionLinkResultLogService.logicDeleteByIdList(delRequest);
    }
}
