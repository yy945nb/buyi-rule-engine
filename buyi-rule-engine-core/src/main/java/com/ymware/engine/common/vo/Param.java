package com.ymware.engine.common.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;

@Data
public class Param<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private T param;
}
