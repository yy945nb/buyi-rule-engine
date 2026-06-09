package com.ymware.engine.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ymware.engine.constants.BaseConstants;
import com.ymware.engine.event.ExecutorConfigRefreshEvent;
import com.ymware.engine.entity.ExpressionExecutorBaseInfo;
import com.ymware.engine.model.request.AddExpressionExecutorRequest;
import com.ymware.engine.model.dto.request.DeleteByIdListRequest;
import com.ymware.engine.model.dto.request.EditExpressionExecutorRequest;
import com.ymware.engine.model.dto.request.QueryExpressionExecutorRequest;
import com.ymware.engine.model.response.ExpressionExecutorBaseDTO;
import com.ymware.engine.model.response.RestResult;
import com.ymware.engine.service.ExpressionExecutorConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *执行器管理
 */
@Tag(name = "执行器管理")
@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(BaseConstants.BASE_PATH + "/executor/info")
public class ExecutorManagerController {
    @Autowired
    private ExpressionExecutorConfigService executorConfigService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    //    @Operation(summary = "添加单个执行器")
    @PostMapping("/addOne")
    public RestResult<ExpressionExecutorBaseDTO> addOne(@RequestBody AddExpressionExecutorRequest addExpressionExecutorRequest) {
        return executorConfigService.addExpressionExecutor(addExpressionExecutorRequest);
    }

    //    @Operation(summary = "编辑单个执行器")
    @PostMapping("/editOne")
    public RestResult<ExpressionExecutorBaseDTO> editOne(@RequestBody EditExpressionExecutorRequest editRequest) {
        return executorConfigService.editExpressionExecutor(editRequest);
    }

    //    @Operation(summary = "查询执行器")
    @PostMapping("/findExecutorList")
    public RestResult<Page<ExpressionExecutorBaseInfo>> findExecutorList(@RequestBody QueryExpressionExecutorRequest queryRequest) {
        final Page<ExpressionExecutorBaseInfo> expressionExecutorBaseInfoPage = executorConfigService.queryExpressionExecutor(queryRequest);
        return RestResult.ok(expressionExecutorBaseInfoPage);
    }

    //    @Operation(summary = "查询单个执行器")
    @PostMapping("/findExecutorInfo")
    public RestResult<ExpressionExecutorBaseDTO> findExecutorList(@RequestParam("id") Long id) {
        final ExpressionExecutorBaseInfo info = executorConfigService.getById(id);
        ExpressionExecutorBaseDTO dto = new ExpressionExecutorBaseDTO();
        BeanUtil.copyProperties(info, dto);
        return RestResult.ok(dto);
    }

    //    @Operation(summary = "批量逻辑删除执行器")
    @PostMapping("/batchDelete")
    public RestResult<?> batchDelete(@RequestBody @Validated DeleteByIdListRequest delRequest) {
        return executorConfigService.batchDeleteByIdList(delRequest);
    }

    //    @Operation(summary = "刷新执行器相关数据")
    @GetMapping("/refreshOne")
    public RestResult<?> refreshOne(@RequestParam("id") Long executorId) {
        eventPublisher.publishEvent(new ExecutorConfigRefreshEvent(executorId));
        return RestResult.ok();
    }


}
