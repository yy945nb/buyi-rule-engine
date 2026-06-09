package com.ymware.engine.vo.condition.group;


import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/8/28
 * @since 1.0.0
 */
@Data
public class SaveOrUpdateConditionGroup {

    private Long id;

    private String name;

    @NotNull
    private Long ruleId;

    private Integer orderNo;

}
