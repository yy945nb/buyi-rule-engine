package com.ymware.engine.vo.condition.group.condition;

import com.ymware.engine.vo.condition.AddConditionRequest;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * 〈SaveConditionAndBindGroupRequest〉
 *
 * @author 丁乾文
 * @date 2021/7/12 1:42 下午
 * @since 1.0.0
 */
@Data
public class SaveConditionAndBindGroupRequest {

    /**
     * 与addConditionRequest绑定
     */
    @NotNull
    private Long conditionGroupId;

    @NotNull
    private Integer orderNo;

    /**
     * 条件信息
     */
    @NotNull
    @Valid
    private AddConditionRequest addConditionRequest;

}
