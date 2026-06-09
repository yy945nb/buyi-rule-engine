package com.ymware.engine.common.exception;

import cn.hutool.core.util.StrUtil;
import com.ymware.engine.common.vo.BaseResult;
import com.ymware.engine.common.enums.ErrorCodeEnum;
import com.ymware.engine.utils.TraceHelper;
import com.ymware.engine.exception.EngineException;
import com.ymware.engine.exception.FunctionException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * API 异常处理器基类
 * 包含 web 和 compute 模块共同的异常处理方法
 */
@Slf4j
public abstract class BaseApiExceptionHandler {

    /**
     * 处理未捕获异常
     */
    @ExceptionHandler(value = Exception.class)
    public BaseResult exception(Exception e) {
        BaseResult result = BaseResult.err();
        log.error("Exception", e);
        // 抛出的未知异常 加上RequestId
        result.setMessage(ErrorCodeEnum.RULE500.getMsg().concat(StringPool.AT).concat(TraceHelper.getRequestId()));
        result.setCode(ErrorCodeEnum.RULE500.getCode());
        return result;
    }

    /**
     * 处理规则引擎异常
     */
    @ExceptionHandler(value = EngineException.class)
    public BaseResult engineException(EngineException e) {
        BaseResult result = BaseResult.err();
        log.warn("EngineException", e);
        result.setMessage(e.getMessage());
        result.setCode(ErrorCodeEnum.RULE8900.getCode());
        return result;
    }

    /**
     * 处理规则函数异常
     */
    @ExceptionHandler(value = FunctionException.class)
    public BaseResult functionException(FunctionException e) {
        BaseResult result = BaseResult.err();
        log.warn("FunctionException", e);
        result.setMessage(Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse(e.getMessage()));
        result.setCode(ErrorCodeEnum.RULE8910.getCode());
        return result;
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(value = {NoHandlerFoundException.class})
    public BaseResult noHandlerFoundException() {
        BaseResult result = BaseResult.err();
        result.setMessage(ErrorCodeEnum.RULE9999404.getMsg());
        result.setCode(ErrorCodeEnum.RULE9999404.getCode());
        return result;
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public BaseResult httpRequestMethodNotSupportedException() {
        BaseResult result = BaseResult.err();
        result.setMessage(ErrorCodeEnum.RULE9999405.getMsg());
        result.setCode(ErrorCodeEnum.RULE9999405.getCode());
        return result;
    }

    /**
     * 处理自定义API异常
     */
    @ExceptionHandler(value = ApiException.class)
    public BaseResult apiException(ApiException e) {
        log.warn("ApiException", e);
        BaseResult result = BaseResult.err();
        result.setMessage(e.getMessage());
        result.setCode(e.getCode());
        return result;
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public BaseResult illegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException", e);
        BaseResult result = BaseResult.err();
        result.setMessage(ErrorCodeEnum.RULE99990100.getMsg());
        result.setCode(ErrorCodeEnum.RULE99990100.getCode());
        return result;
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(value = BindException.class)
    public BaseResult bindException(BindException e) {
        log.warn("BindException", e);
        BaseResult result = BaseResult.err();
        FieldError error = e.getFieldError();
        result.setMessage(Optional.ofNullable(error).map(FieldError::getDefaultMessage).orElse(ErrorCodeEnum.RULE99990100.getMsg()));
        result.setCode(ErrorCodeEnum.RULE99990100.getCode());
        return result;
    }

    /**
     * 处理媒体类型不支持异常
     */
    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    public BaseResult httpMediaTypeNotSupportedException() {
        BaseResult result = BaseResult.err();
        result.setMessage(ErrorCodeEnum.RULE99990001.getMsg());
        result.setCode(ErrorCodeEnum.RULE99990001.getCode());
        return result;
    }

    /**
     * 处理请求正文缺失异常
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public BaseResult httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException", e);
        BaseResult result = BaseResult.err();
        result.setMessage(ErrorCodeEnum.RULE10010003.getMsg());
        result.setCode(ErrorCodeEnum.RULE10010003.getCode());
        return result;
    }

    /**
     * 处理方法参数无效异常
     */
    @SneakyThrows
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public BaseResult methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException" + e.getMessage());
        BaseResult result = BaseResult.err();
        Field source = ObjectError.class.getDeclaredField("source");
        if (!Modifier.isPublic(source.getModifiers())) {
            source.setAccessible(true);
        }
        ConstraintViolation<?> constraintViolation = (ConstraintViolation<?>) source.get(e.getBindingResult().getFieldError());
        String messageTemplate = constraintViolation.getMessageTemplate();
        // 如果使用默认的{jakarta.validation.constraints.***.message}
        if (messageTemplate.startsWith(StrUtil.DELIM_START) && messageTemplate.endsWith(StrUtil.DELIM_END)) {
            result.setMessage(constraintViolation.getPropertyPath().toString() + " " + constraintViolation.getMessage());
        } else {
            result.setMessage(constraintViolation.getMessage());
        }
        result.setCode(ErrorCodeEnum.RULE99990002.getCode());
        return result;
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public BaseResult constraintViolationException(ConstraintViolationException e) {
        log.warn("ConstraintViolationException", e);
        BaseResult result = BaseResult.err();
        List<ConstraintViolation<?>> arrayList = new ArrayList<>(e.getConstraintViolations());
        ConstraintViolation<?> constraintViolation = arrayList.get(0);
        result.setMessage(constraintViolation.getMessage());
        result.setCode(ErrorCodeEnum.RULE99990100.getCode());
        return result;
    }
}
