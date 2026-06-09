package com.ymware.engine.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ymware.engine.entity.ExpressionExecutorBaseInfo;
import com.ymware.engine.model.request.AddExpressionExecutorRequest;
import com.ymware.engine.model.dto.request.DeleteByIdListRequest;
import com.ymware.engine.model.dto.request.EditExpressionExecutorRequest;
import com.ymware.engine.model.dto.request.QueryExpressionExecutorRequest;
import com.ymware.engine.model.response.ExpressionExecutorBaseDTO;
import com.ymware.engine.model.response.RestResult;

/**
 * 服务类
 */
public interface ExpressionExecutorConfigService extends IService<ExpressionExecutorBaseInfo> {

    RestResult<ExpressionExecutorBaseDTO> addExpressionExecutor(AddExpressionExecutorRequest addRequest);

    RestResult<ExpressionExecutorBaseDTO> editExpressionExecutor(EditExpressionExecutorRequest editRequest);

    Page<ExpressionExecutorBaseInfo> queryExpressionExecutor(QueryExpressionExecutorRequest queryRequest);

    RestResult<?> batchDeleteByIdList(DeleteByIdListRequest delRequest);

    /**
     * 查询执行对象
     *
     * @param serviceName
     * @param businessCode
     * @param executorCode
     * @return
     */
    public ExpressionExecutorBaseDTO queryExecutorInfo(String serviceName, String businessCode, String executorCode);
}
