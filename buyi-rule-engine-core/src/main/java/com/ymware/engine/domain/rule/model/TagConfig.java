package com.ymware.engine.domain.rule.model;

import lombok.Data;

/**
 * 标签配置项
 */
@Data
public class TagConfig<T> {
    /**
     * id
     */
    private String id;

    /**
     * 配置项CODE
     */
    private String code;

    /**
     * 配置项值
     */
    private T configValue;

}
