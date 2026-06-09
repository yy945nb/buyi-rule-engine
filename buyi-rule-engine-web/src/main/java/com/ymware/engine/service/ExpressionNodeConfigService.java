package com.ymware.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ymware.engine.entity.ExpressionNodeConfig;
import com.ymware.engine.model.dto.request.AddExpressionNodeRequest;
import com.ymware.engine.model.dto.request.DeleteByIdListRequest;
import com.ymware.engine.model.request.EditExpressionNodeRequest;
import com.ymware.engine.model.dto.request.QueryExpressionNodeRequest;
import com.ymware.engine.model.response.ExpressionNodeDTO;
import com.ymware.engine.model.response.RestResult;

import java.util.List;

/**
 * 引擎节点信息 服务类
 */
public interface ExpressionNodeConfigService extends IService<ExpressionNodeConfig> {

    RestResult<ExpressionNodeDTO> addExpressionNode(AddExpressionNodeRequest addRequest);

    RestResult<ExpressionNodeDTO> editExpressionNode(EditExpressionNodeRequest editRequest);

    RestResult<List<ExpressionNodeDTO>> queryExpressionNode(QueryExpressionNodeRequest queryRequest);

    RestResult<?> batchDeleteByIdList(DeleteByIdListRequest delRequest);
}
