package com.ymware.engine.model.dto.response;

import com.ymware.engine.entity.ExpressionTraceLogIndex;
import com.ymware.engine.entity.ExpressionTraceLogInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 表达式追踪日志详情
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExpressionTraceInfoDTO extends ExpressionTraceLogIndex {

    private List<ExpressionTraceLogInfo> traceLogInfoList;

}
