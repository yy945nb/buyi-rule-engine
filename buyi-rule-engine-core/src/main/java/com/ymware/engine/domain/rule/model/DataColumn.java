package com.ymware.engine.domain.rule.model;

import lombok.Data;

@Data
public class DataColumn {

    private Integer colIndex;

    private String colName;

    private String colType;

    private Object colValue;
}
