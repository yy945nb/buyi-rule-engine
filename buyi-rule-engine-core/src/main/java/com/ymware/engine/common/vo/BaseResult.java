package com.ymware.engine.common.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * Base result wrapper - migrated from cn.ruleengine:common
 */
@Data
public class BaseResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;

    public static <T> BaseResult<T> ok() {
        BaseResult<T> result = new BaseResult<>();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    public static <T> BaseResult<T> ok(T data) {
        BaseResult<T> result = ok();
        result.setData(data);
        return result;
    }

    public static <T> BaseResult<T> err() {
        BaseResult<T> result = new BaseResult<>();
        result.setCode(500);
        result.setMessage("error");
        return result;
    }

    public static <T> BaseResult<T> err(String msg) {
        BaseResult<T> result = err();
        result.setMessage(msg);
        return result;
    }

    public static <T> BaseResult<T> err(int code, String msg) {
        BaseResult<T> result = err();
        result.setCode(code);
        result.setMessage(msg);
        return result;
    }
}
