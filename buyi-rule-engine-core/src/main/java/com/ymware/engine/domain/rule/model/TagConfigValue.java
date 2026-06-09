package com.ymware.engine.domain.rule.model;

import lombok.Data;

/**
 * 标签配置项值
 */
@Data
public class TagConfigValue {

    /**
     * 配置项CODE
     */
    private String code;

    /**
     * 配置项值
     */
    private String configValue;

    public TagConfigValue(String code, String configValue) {
        this.code = code;
        this.configValue = configValue;
    }

}
