package com.ymware.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.ymware.engine.permission.IgnorePermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 规则执行日志表
 * </p>
 *
 * @author sanyuan
 * @since 2025-11-28
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("tag_rule_execution_log")
@Schema(description = "TagRuleExecutionLog对象 - 规则执行日志表")
@IgnorePermission
public class TagRuleExecutionLog  {

    @Schema(description = "规则ID")
    @TableField("rule_id")
    private Long ruleId;

    @Schema(description = "执行状态")
    @TableField("execution_status")
    private String executionStatus;

    @Schema(description = "影响用户数")
    @TableField("affected_users")
    private Integer affectedUsers;

    @Schema(description = "错误信息")
    @TableField("error_message")
    private String errorMessage;

    @Schema(description = "开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;

    @Schema(description = "执行时长(秒)")
    @TableField("execution_duration")
    private Integer executionDuration;


}
