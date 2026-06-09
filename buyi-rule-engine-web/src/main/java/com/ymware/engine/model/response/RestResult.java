package com.ymware.engine.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "响应参数")
public class RestResult<T> implements Serializable {

    /**
     * 返回码
     */
    @Schema(description = "响应状态码")
    private Integer code;

    /**
     * 返回信息
     */
    @Schema(description = "返回信息")
    private String message;

    /**
     * 返回数据
     */
    @Schema(description = "返回数据")
    private T data;

    /**
     * 成功返回值
     *
     * @return
     */
    public static <T> RestResult<T> ok() {
        return ok(null);
    }

    public Boolean isOk() {
        return 200L == getCode();
    }

    /**
     * 成功返回值
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> RestResult<T> ok(T data) {
        return ok(200, "SUCCESS", data);
    }

    public static <T> RestResult<T> ok(Integer code, String msg, T data) {
        RestResult<T> result = new RestResult<T>();
        result.setCode(code);
        result.setMessage(msg);
        result.setData(data);
        return result;
    }

    public static <T> RestResult<T> ok(Integer code, String msg) {
        return ok(code, msg, null);
    }

    public static <T> RestResult<T> failed(String msg) {
        return failed(500, msg);
    }

    public static <T> RestResult<T> failed(Integer code, String msg) {
        return failed(code, msg, null);
    }

    public static <T> RestResult<T> failed(Integer code, String msg, T data) {
        RestResult<T> result = new RestResult<T>();
        result.setCode(code);
        result.setMessage(msg);
        result.setData(data);
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
