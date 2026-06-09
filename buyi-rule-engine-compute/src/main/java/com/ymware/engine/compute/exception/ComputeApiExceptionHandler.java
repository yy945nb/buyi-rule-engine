package com.ymware.engine.compute.exception;

import com.ymware.engine.common.exception.BaseApiExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Compute 模块异常处理器
 * 继承基类，无额外方法
 */
@Slf4j
@RestControllerAdvice
public class ComputeApiExceptionHandler extends BaseApiExceptionHandler {
    // 所有通用异常处理已在基类中定义
}
