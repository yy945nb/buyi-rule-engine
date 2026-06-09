package com.ymware.engine.domain.rule.model;

import lombok.Data;

/**
 * 规则指标
 */
@Data
public class RuleMetric {

    /**
     * 指标编码
     */
    private String metricCode;

    /**
     * 指标数据
     */
    private Object metricData;

    /**
     * 指标类型
     */
    private Integer metricType;

}
