package com.ymware.engine.vo.condition;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/9/7
 * @since 1.0.0
 */
@Data
public class ConditionGroupCondition {

    private Long id;

    @NotNull
    private Integer orderNo;

    @Valid
    private ConditionBody condition;

}
