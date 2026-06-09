package com.ymware.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ymware.engine.entity.ExpressionVariableConfig;
import com.ymware.engine.model.dto.request.QueryExpressionVariableRequest;
import com.ymware.engine.model.response.ExpressionVariableDTO;
import com.ymware.engine.model.response.RestResult;
import com.ymware.engine.model.variable.VariableInfoDto;

import java.util.List;

/**
 * <p>
 * 表达式引擎通用变量配置表 服务类
 * </p>
 *
 * @author bsy
 * @since 2022-06-08
 */
public interface ExpressionVariableConfigService extends IService<ExpressionVariableConfig> {

    RestResult<List<ExpressionVariableDTO>> queryExpressionVariable(QueryExpressionVariableRequest queryRequest);

    VariableInfoDto getKeyInfo(String key);

    boolean refresh(String serviceName, List<VariableInfoDto> variableInfoDto);

    List<VariableInfoDto> loadAllVariableDefined();
}
