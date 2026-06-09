package com.ymware.engine.controller.exception;

import com.ymware.engine.common.vo.BaseResult;
import com.ymware.engine.common.enums.ErrorCodeEnum;
import com.ymware.engine.common.exception.BaseApiExceptionHandler;
import com.ymware.engine.exception.ConditionException;
import com.ymware.engine.exception.DataPermissionException;
import com.ymware.engine.exception.LoginException;
import com.ymware.engine.exception.ReSubmitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ValidationException;

/**
 * Web 模块异常处理器
 * 继承基类，添加 web 独有的异常处理
 */
@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends BaseApiExceptionHandler {

    /**
     * 数据权限异常
     */
    @ExceptionHandler(value = DataPermissionException.class)
    public BaseResult dataPermissionException(DataPermissionException e) {
        log.warn("DataPermissionException: {}", e.getMessage());
        BaseResult result = BaseResult.err();
        result.setMessage(e.getMessage());
        result.setCode(ErrorCodeEnum.RULE8930.getCode());
        return result;
    }

    /**
     * 条件配置异常
     */
    @ExceptionHandler(value = ConditionException.class)
    public BaseResult conditionException(ConditionException e) {
        log.warn("ConditionException: {}", e.getMessage());
        BaseResult result = BaseResult.err();
        result.setMessage(e.getMessage());
        result.setCode(ErrorCodeEnum.RULE8920.getCode());
        return result;
    }

    /**
     * 验证异常
     */
    @ExceptionHandler(value = ValidationException.class)
    public BaseResult validationException(ValidationException e) {
        log.warn("ValidationException: {}", e.getMessage());
        BaseResult result = BaseResult.err();
        result.setMessage(e.getMessage());
        result.setCode(ErrorCodeEnum.RULE99990100.getCode());
        return result;
    }

    /**
     * 登录异常
     */
    @ExceptionHandler(value = LoginException.class)
    public BaseResult loginException(LoginException e) {
        log.debug("loginException: {}", e.getMessage());
        BaseResult result = BaseResult.err();
        result.setMessage(e.getMessage());
        result.setCode(ErrorCodeEnum.RULE99990101.getCode());
        return result;
    }

    /**
     * 重复提交异常
     */
    @ExceptionHandler(value = ReSubmitException.class)
    public BaseResult reSubmitException(ReSubmitException e) {
        BaseResult result = BaseResult.err();
        result.setMessage(e.getMessage());
        result.setCode(e.getCode());
        return result;
    }
}
