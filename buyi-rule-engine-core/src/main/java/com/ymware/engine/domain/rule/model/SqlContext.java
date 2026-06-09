package com.ymware.engine.domain.rule.model;

import lombok.Data;

import java.util.Map;

/**
 * 数据来源
 */
@Data
public class SqlContext {

    /**
     * SQL
     */
    private String sqlExpress;


    /**
     * SQL参数
     */
    private Map<String, Object> params;

    /**
     * sql对应的数据源
     */
    private String dataSourceCode;


}
