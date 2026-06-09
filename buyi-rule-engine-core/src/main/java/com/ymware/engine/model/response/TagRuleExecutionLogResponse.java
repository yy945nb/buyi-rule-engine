package com.ymware.engine.model.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 规则执行日志表 query
 * </p>
 *
 * @author sanyuan
 * @since 2025-11-28
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "TagRuleExecutionLogResponse对象 - 规则执行日志表")
public class TagRuleExecutionLogResponse {

    @Schema(description = "规则ID")
    private Long ruleId;

    @Schema(description = "执行状态")
    private Boolean executionStatus;

    @Schema(description = "影响用户数")
    private Integer affectedUsers;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "执行时长(秒)")
    private Integer executionDuration;


}

