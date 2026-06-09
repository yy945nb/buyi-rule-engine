package com.ymware.engine.common.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class Rows<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long total;
    private List<T> rows;
    private PageResponse pageResponse;

    public Rows() {}

    public Rows(List<T> rows, PageResponse pageResponse) {
        this.rows = rows;
        this.pageResponse = pageResponse;
        if (pageResponse != null) {
            this.total = pageResponse.getTotal();
        }
    }

    public Rows(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }
}
