package com.ymware.engine.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class PageBase implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private Long total;
}
