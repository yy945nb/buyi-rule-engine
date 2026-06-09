package com.ymware.engine.domain.rule.model;

import lombok.Data;

import java.util.Map;

/**
 * 业务规则校验上下文
 */
@Data
public class RuleCheckContext {

    /**
     * 校验的业务数据（单jvm中可支持强转到任何类型，跨服务调用会自动转为LinkedHashMap，跨服务调用时不支持强转）
     */
    private Map<String, Object> checkRuleData;

}
