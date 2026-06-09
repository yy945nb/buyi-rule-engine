package com.ymware.engine.vo.rule;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 〈SaveRuleAndBindRuleSetRequest〉
 *
 * @author 丁乾文
 * @date 2021/7/28 1:03 下午
 * @since 1.0.0
 */
@Data
public class SaveRuleAndBindRuleSetRequest {

    @NotNull
    private Long ruleSetId;

    @NotNull
    private RuleBody ruleBody;

}
