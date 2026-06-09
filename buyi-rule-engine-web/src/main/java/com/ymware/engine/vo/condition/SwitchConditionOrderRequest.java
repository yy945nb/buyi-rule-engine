package com.ymware.engine.vo.condition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/***
 * 交换条件顺序
 * @author niuxiangqian
 * @version 1.0
 * @since 2021/7/17 5:53 下午
 **/
@Data
public class SwitchConditionOrderRequest {

    public static final Integer TOP = 0;

    public static final Integer BOTTOM = 1;

    @NotNull
    @Schema(description = "原来的id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long fromId;

    @NotNull
    private Long fromConditionGroupId;

    /**
     * 可以不传，只有当跨条件组拖拽时候  目前条件组没有任何条件时
     */
    @Schema(description = "目标id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long toId;

    /**
     * 0 是 toId的上面  1是toId的下面
     */
    private Integer toType = 1;

    @NotNull
    private Long toConditionGroupId;

}
