package com.ymware.engine.vo.condition;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/7/14
 * @since 1.0.0
 */
@Data
public class ConfigBean {

    @Valid
    private ConfigValue leftValue;

    @NotBlank
    private String symbol;

    @Valid
    private ConfigValue rightValue;

}
