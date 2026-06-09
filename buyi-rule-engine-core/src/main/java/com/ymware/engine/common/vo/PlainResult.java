package com.ymware.engine.common.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * Plain result wrapper - extends BaseResult for compatibility
 */
@Data
public class PlainResult<T> extends BaseResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static <T> PlainResult<T> ok() {
        PlainResult<T> result = new PlainResult<>();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    public static <T> PlainResult<T> ok(T data) {
        PlainResult<T> result = ok();
        result.setData(data);
        return result;
    }

    public static <T> PlainResult<T> err() {
        PlainResult<T> result = new PlainResult<>();
        result.setCode(500);
        result.setMessage("error");
        return result;
    }

    public static <T> PlainResult<T> err(String msg) {
        PlainResult<T> result = err();
        result.setMessage(msg);
        return result;
    }
}
