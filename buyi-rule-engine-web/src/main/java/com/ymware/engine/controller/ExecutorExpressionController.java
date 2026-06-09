package com.ymware.engine.controller;


import cn.hutool.core.bean.BeanUtil;
import com.ymware.engine.constants.BaseConstants;
import com.ymware.engine.entity.ExpressionExecutorInfoConfig;
import com.ymware.engine.model.request.AddExpressionConfigRequest;
import com.ymware.engine.model.dto.request.DeleteByIdListRequest;
import com.ymware.engine.model.dto.request.EditExpressionConfigRequest;
import com.ymware.engine.model.dto.request.QueryExpressionConfigRequest;
import com.ymware.engine.model.dto.response.ExpressionExecutorDetailConfigDTO;
import com.ymware.engine.model.response.RestResult;
import com.ymware.engine.service.ExpressionConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 表达式配置 前端控制器
 * </p>
 *
 * @author bsy
 * @since 2022-06-12
 */
@Tag(name = "执行器表达式管理")
@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(BaseConstants.BASE_PATH + "/executor/expression")
public class ExecutorExpressionController {
    @Autowired
    private ExpressionConfigService expressionConfigService;

    @Operation(summary = "添加单个表达式")
    @PostMapping("/addOne")
    public RestResult<ExpressionExecutorDetailConfigDTO> addOne(@Validated @RequestBody AddExpressionConfigRequest addRequest) {
        return expressionConfigService.addExpression(addRequest);
    }

    @Operation(summary = "编辑单个表达式")
    @PostMapping("/editOne")
    public RestResult<ExpressionExecutorDetailConfigDTO> editOne(@RequestBody @Validated EditExpressionConfigRequest editRequest) {
        return expressionConfigService.editExpression(editRequest);
    }

    @Operation(summary = "修改父子关系")
    @PostMapping("/editParentId")
    public RestResult<Boolean> editParentId(@RequestBody @Validated EditExpressionConfigRequest editRequest) {
        ExpressionExecutorInfoConfig config = new ExpressionExecutorInfoConfig();
        config.setId(editRequest.getId());
        config.setParentId(editRequest.getParentId());
        return RestResult.ok(expressionConfigService.updateById(config));
    }

    @Operation(summary = "复制节点")
    @PostMapping("/copyNode")
    public RestResult<Boolean> copyNode(@RequestBody @Validated EditExpressionConfigRequest editRequest) {
        ExpressionExecutorInfoConfig config = new ExpressionExecutorInfoConfig();
        config.setId(editRequest.getId());
        config.setParentId(editRequest.getParentId());
        return RestResult.ok(expressionConfigService.copyNode(config));
    }

    @Operation(summary = "查询表达式")
    @PostMapping("/findExpressionList")
    public RestResult<List<ExpressionExecutorDetailConfigDTO>> findExpressionList(@RequestBody QueryExpressionConfigRequest queryRequest) {
        return expressionConfigService.queryExpression(queryRequest);
    }

    @Operation(summary = "查询单个表达式")
    @PostMapping("/findExpressionInfo")
    public RestResult<ExpressionExecutorDetailConfigDTO> findExpressionInfo(@RequestParam("id") Long id) {
        final ExpressionExecutorInfoConfig config = expressionConfigService.getById(id);
        ExpressionExecutorDetailConfigDTO expressionExecutorDetailConfigDTO = new ExpressionExecutorDetailConfigDTO();
        BeanUtil.copyProperties(config, expressionExecutorDetailConfigDTO);
        return RestResult.ok(expressionExecutorDetailConfigDTO);
    }

    @Operation(summary = "批量逻辑删除表达式")
    @PostMapping("/batchDelete")
    public RestResult<?> batchDelete(@RequestBody @Validated DeleteByIdListRequest delRequest) {
        return expressionConfigService.batchDeleteByIdList(delRequest);
    }


//    @Operation(summary = "表达式翻译")
//    @PostMapping("/translate")
//    public RestResult<TranslateResult> translate(@RequestBody ExpressionApiModel apiModel) {
//        ExpressionService expressionService = executorFactory.getExpressionService();
//        TranslateResult translate = expressionService.translate(apiModel.getText());
//        return RestResult.ok(translate);
//    }
//
//    @Operation(summary = "表达式验证")
//    @PostMapping("/validator")
//    public RestResult<ValidatorResult> validator(@RequestBody ExpressionApiModel apiModel) {
//        ExpressionService expressionService = executorFactory.getExpressionService();
//        ValidatorResult validator = expressionService.validator(apiModel.getText());
//        return RestResult.ok(validator);
//    }
}
