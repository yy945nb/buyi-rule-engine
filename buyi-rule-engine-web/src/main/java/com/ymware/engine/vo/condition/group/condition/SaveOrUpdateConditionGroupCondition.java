package com.ymware.engine.vo.condition.group.condition;

import lombok.Data;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/8/28
 * @since 1.0.0
 */
@Data
public class SaveOrUpdateConditionGroupCondition {
    private Long id;

    private Long conditionId;

    private Long conditionGroupId;

    private Integer orderNo;
}
