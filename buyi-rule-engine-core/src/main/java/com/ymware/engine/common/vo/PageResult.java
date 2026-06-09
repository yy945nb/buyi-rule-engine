package com.ymware.engine.common.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class PageResult<T> extends BaseResult<Rows<T>> implements Serializable {
    private static final long serialVersionUID = 1L;

    public static <T> PageResult<T> ok(Rows<T> data) {
        PageResult<T> result = new PageResult<>();
        result.setData(data);
        return result;
    }
}
