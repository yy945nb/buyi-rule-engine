package com.ymware.engine.common.vo;

import com.ymware.engine.entity.PageBase;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PageRequest<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private PageBase page = new PageBase();
    private T query;
    private List<OrderBy> orders;

    @Data
    public static class OrderBy implements Serializable {
        private static final long serialVersionUID = 1L;
        private String columnName;
        private Boolean asc = true;
    }
}
