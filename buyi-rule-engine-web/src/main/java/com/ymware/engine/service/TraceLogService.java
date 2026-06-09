package com.ymware.engine.service;

import com.ymware.engine.model.dto.trace.TraceLogDto;

/**
 * 链路日志表
 * @author liukaixiong
 * @date : 2022/6/20 - 10:53
 */
public interface TraceLogService {

    /**
     * 链路日志追踪
     * @param traceLogDto
     * @return
     */
    public Boolean log(TraceLogDto traceLogDto);

    public Boolean success(TraceLogDto traceLogDto);
}
