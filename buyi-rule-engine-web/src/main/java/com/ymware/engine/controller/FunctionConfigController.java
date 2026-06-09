package com.ymware.engine.controller;


import com.ymware.engine.constants.BaseConstants;
import com.ymware.engine.model.request.EditExpressionFunctionRequest;
import com.ymware.engine.model.dto.request.QueryExpressionFunctionRequest;
import com.ymware.engine.model.dto.response.ExpressionFunctionDTO;
import com.ymware.engine.model.response.RestResult;
import com.ymware.engine.service.ExpressionFunctionConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 函数配置表 前端控制器
 * </p>
 *
 * @author bsy
 * @since 2022-06-08
 */
@Tag(name = "函数管理")
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(BaseConstants.BASE_PATH + "/function")
@Validated
public class FunctionConfigController {
    @Autowired
    private ExpressionFunctionConfigService functionConfigService;

//    @Operation(summary = "添加单个函数")
//    @PostMapping("/addOne")
//    public RestResult<ExpressionFunctionDTO> addOne(@Valid @RequestBody AddExpressionFunctionRequest addExpressionFunctionRequest) {
//        return functionConfigService.addExpressionFunction(addExpressionFunctionRequest);
//    }
//
//    @Operation(summary = "编辑单个函数")
//    @PostMapping("/editOne")
//    public RestResult<ExpressionFunctionDTO> editOne(@RequestBody @Valid EditExpressionFunctionRequest editRequest) {
//        return functionConfigService.editExpressionFunction(editRequest);
//    }

    @Operation(summary = "查询函数")
    @PostMapping("/findFunc")
    public RestResult<List<ExpressionFunctionDTO>> findFuncList(@RequestBody QueryExpressionFunctionRequest queryRequest) {
        return functionConfigService.queryExpressionFunction(queryRequest);
    }
//
//    @Operation(summary = "批量逻辑删除函数")
//    @PostMapping("/batchDelete")
//    public RestResult<?> batchDelete(@RequestBody @Valid DeleteByIdListRequest delRequest) {
//        return functionConfigService.batchDeleteByIdList(delRequest);
//    }
}
