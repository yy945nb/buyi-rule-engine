package com.ymware.engine.vo.rule.general;

import com.ymware.engine.vo.condition.ConfigValue;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * 〈SaveActionRequest〉
 *
 * @author 丁乾文
 * @date 2021/7/12 5:34 下午
 * @since 1.0.0
 */
@Data
public class SaveDefaultActionRequest {

    @NotNull
    private Long generalRuleId;

    /**
     * 结果配置信息
     */
    @NotNull
    @Valid
    private ConfigValue configValue;


}
