package com.ymware.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ymware.engine.entity.ExpressionFunctionConfig;
import com.ymware.engine.model.dto.request.QueryExpressionFunctionRequest;
import com.ymware.engine.model.dto.response.ExpressionFunctionDTO;
import com.ymware.engine.model.response.RestResult;
import com.ymware.engine.model.dto.function.FunctionInfoDto;

import java.util.List;

/**
 * 函数配置表 服务类
 */
public interface ExpressionFunctionConfigService extends IService<ExpressionFunctionConfig> {

    RestResult<List<ExpressionFunctionDTO>> queryExpressionFunction(QueryExpressionFunctionRequest queryRequest);

    FunctionInfoDto getKeyInfo(String key);

    boolean refresh(String serviceName, List<FunctionInfoDto> variableInfoDto);

    List<FunctionInfoDto> loadAllFunctionDefined();
}
