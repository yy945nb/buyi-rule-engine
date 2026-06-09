package com.ymware.engine.vo.condition.group.condition;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 〈SaveConditionAndBindGroupRequest〉
 *
 * @author 丁乾文
 * @date 2021/7/12 1:42 下午
 * @since 1.0.0
 */
@Data
public class DeleteConditionAndBindGroupRefRequest {

    /**
     * 属于哪一个规则，用来拦截器取参数
     */
    @NotNull
    private Long conditionId;

    /**
     * 条件组关系id
     */
    @NotNull
    private Long conditionGroupRefId;


}
