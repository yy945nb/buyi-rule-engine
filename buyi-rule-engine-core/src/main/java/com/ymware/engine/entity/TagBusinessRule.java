package com.ymware.engine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ymware.engine.permission.IgnorePermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 业务规则表
 * </p>
 *
 * @author sanyuan
 * @since 2025-11-28
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("tag_business_rule")
@Schema(description = "TagBusinessRule对象 - 业务规则表")
@IgnorePermission
public class TagBusinessRule{

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @TableField("id")
    private Long id;

    @Schema(description = "目标标签ID")
    @TableField("target_tag_id")
    private Long targetTagId;

    @Schema(description = "规则名称")
    @TableField("rule_name")
    private String ruleName;

    @Schema(description = "规则编码")
    @TableField("rule_code")
    private String ruleCode;

    @Schema(description = "cron表达式")
    @TableField("cron_expression")
    private String cronExpression;

    @Schema(description = "调度表达式")
    @TableField("schedule_express")
    private String scheduleExpress;

    @Schema(description = "规则SQL")
    @TableField("rule_sql")
    private String ruleSql;

    @Schema(description = "数据源编码")
    @TableField("data_source_code")
    private String dataSourceCode;

    @Schema(description = "规则描述")
    @TableField("description")
    private String description;

    @Schema(description = "规则类型")
    @TableField("rule_type")
    private Integer ruleType;

    @Schema(description = "条件表达式")
    @TableField("condition_expression")
    private String conditionExpression;

    @Schema(description = "执行动作")
    @TableField("action_expression")
    private String actionExpression;

    @Schema(description = "执行优先级")
    @TableField("priority")
    private Integer priority;

    @Schema(description = "生效时间")
    @TableField("effective_time")
    private LocalDate effectiveTime;

    @Schema(description = "失效时间")
    @TableField("expiry_time")
    private LocalDate expiryTime;

    @Schema(description = "是否激活")
    @TableField("is_active")
    private Integer active;

    @Schema(description = "最后执行时间")
    @TableField("last_executed_at")
    private LocalDateTime lastExecutedAt;

    @Schema(description = "执行次数")
    @TableField("execution_count")
    private Integer executionCount;

    @Schema(description = "创建人")
    @TableField("create_by")
    private Long createBy;

    @TableField("create_time")
    private Date createTime;

    @Schema(description = "更新人")
    @TableField("update_by")
    private Long updateBy;

    @TableField("update_time")
    private Date updateTime;

}
