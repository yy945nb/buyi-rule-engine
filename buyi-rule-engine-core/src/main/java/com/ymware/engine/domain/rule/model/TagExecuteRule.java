package com.ymware.engine.domain.rule.model;

import lombok.Data;

import java.util.List;

/**
 * 标签执行规则
 */
@Data
public class TagExecuteRule {

    private String id;

    private String ruleCode;

    private String ruleName;

    /**
     * 规则表达式
     */
    private String ruleExpress;

    /**
     * 定时调度表达式，支持Con表达式
     */
    private String scheduleExpress;

    /**
     * 标签内容
     */
    private TagContent tagContent;

    /**
     * sql内容，多个sql以分号分割，格式为：dataSource:sql1;datasource:sql2
     */
    private String sqlExpress;

    /**
     * 数据源编码
     */
    private String datasourceCode;

    private String ruleType;

    private Boolean isEnable;

    /**
     * SQL内容（监控指标从SQL结果中提取）
     */
    private List<SqlContext> sqlContentList;
}
