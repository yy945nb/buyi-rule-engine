package com.ymware.engine.vo.rule.general;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 〈DefaultActionSwitchRequest〉
 *
 * @author 丁乾文
 * @date 2021/7/15 11:16 下午
 * @since 1.0.0
 */
@Data
public class DefaultActionSwitchRequest {

    @NotNull
    private Long generalRuleId;

    @NotNull
    private Integer enableDefaultAction;

}
