package com.ymware.engine.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ymware.engine.model.dto.ExpressionExecutorResultDTO;
import com.ymware.engine.entity.ExpressionTraceLogIndex;
import com.ymware.engine.model.request.QueryExpressionTraceRequest;
import com.ymware.engine.model.dto.response.ExpressionTraceInfoDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liukx
 * @since 2024-07-18
 */
public interface ExpressionTraceLogIndexService extends IService<ExpressionTraceLogIndex> {

    boolean addTraceLog(List<ExpressionExecutorResultDTO> request);

    Page<ExpressionTraceLogIndex> queryExpressionTraceLogList(QueryExpressionTraceRequest queryRequest);

    ExpressionTraceInfoDTO getTraceInfoList(Long id);

    /**
     * 配置编号
     *
     * @param expressionId 表达式编号
     * @return
     */
    ExpressionTraceLogIndex getExpressionSampleBody(Long expressionId);
}
