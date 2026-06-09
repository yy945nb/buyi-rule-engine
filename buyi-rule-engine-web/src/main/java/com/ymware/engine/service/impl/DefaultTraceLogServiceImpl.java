package com.ymware.engine.service.impl;

import com.ymware.engine.service.ExpressionLinkResultLogService;
import com.ymware.engine.service.GlobalTraceLogService;
import com.ymware.engine.service.TraceLogService;
import com.ymware.engine.model.dto.trace.TraceLogDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 默认的链路日志实现
 */
@Slf4j
@Service
public class DefaultTraceLogServiceImpl implements TraceLogService {
    @Autowired
    private GlobalTraceLogService traceLogService;

    @Autowired
    private ExpressionLinkResultLogService resultLogService;

    @Override
    public Boolean log(TraceLogDto traceLog) {
//        ExpressionGlobalTraceLog globalLog = new ExpressionGlobalTraceLog();
//        globalLog.setBusinessCode(traceLog.getBusinessCode());
//        globalLog.setEventCode(traceLog.getEventType());
//        globalLog.setExecuteSuccess(traceLog.isException());
//        globalLog.setServiceName(traceLog.getServiceName());
//        globalLog.setResultDescription(traceLog.getContent());
//        globalLog.setLinkNo(traceLog.getLogId());
//        globalLog.setUniqueNo(traceLog.getUnionId());
//        String body = Jsons.toJsonString(globalLog);
//        log.info("链路追踪详情：{}", body);
//        Boolean result = CodeUtils.safeInvoker(() -> traceLogService.save(globalLog));
//        if(result == null || !result){
//            log.warn("新增链路日志失败：{}",body);
//        }
        return true;
    }

    @Override
    public Boolean success(TraceLogDto traceLog) {
//        ExpressionLinkResultLog globalLog = new ExpressionLinkResultLog();
//        globalLog.setBusinessCode(traceLog.getBusinessCode());
//        globalLog.setEventCode(traceLog.getEventType());
//        globalLog.setServiceName(traceLog.getServiceName());
//        globalLog.setResultDescription(traceLog.getContent());
//        globalLog.setLinkNo(traceLog.getLogId());
//        globalLog.setUniqueNo(traceLog.getUnionId());
//        String body = Jsons.toJsonString(globalLog);
//        Boolean result = CodeUtils.safeInvoker(() -> resultLogService.save(globalLog));
//        if(result == null || !result){
//            log.warn("新增链路日志失败：{}",body);
//        }
        return true;
    }
}
