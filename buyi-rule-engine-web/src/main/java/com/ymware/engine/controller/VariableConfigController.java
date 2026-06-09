package com.ymware.engine.controller;


import com.ymware.engine.constants.BaseConstants;
import com.ymware.engine.model.dto.request.QueryExpressionVariableRequest;
import com.ymware.engine.model.response.ExpressionVariableDTO;
import com.ymware.engine.model.response.RestResult;
import com.ymware.engine.service.ExpressionVariableConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 表达式引擎通用变量配置表 前端控制器
 * </p>
 *
 * @author bsy
 * @since 2022-06-08
 */
@Tag(name = "变量管理")
@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(BaseConstants.BASE_PATH + "/variable")
public class VariableConfigController {

    @Autowired
    private ExpressionVariableConfigService variableConfigService;

    @Operation(summary = "查询注册变量")
    @PostMapping("/findVar")
    public RestResult<List<ExpressionVariableDTO>> findFuncList(@RequestBody QueryExpressionVariableRequest queryRequest) {
        return variableConfigService.queryExpressionVariable(queryRequest);
    }

}
