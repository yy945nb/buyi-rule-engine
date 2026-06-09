package com.ymware.engine.model.dto;


import com.ymware.engine.domain.rule.model.SqlContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
@Schema(description = "TagBusinessRuleDTO对象 - 业务规则表")
public class TagBusinessRuleDto {

    private Long id;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "cron表达式")
    private String cronExpression;

    @Schema(description = "规则描述")
    private String description;

    @Schema(description = "规则类型")
    private Integer ruleType;

    @Schema(description = "条件表达式")
    private String conditionExpression;

    @Schema(description = "执行动作")
    private String actionExpression;

    @Schema(description = "目标标签ID")
    private Long targetTagId;

    @Schema(description = "执行优先级")
    private Integer priority;

    @Schema(description = "生效时间")
    private LocalDate effectiveTime;

    @Schema(description = "失效时间")
    private LocalDate expiryTime;

    @Schema(description = "是否激活")
    private Boolean active;

    @Schema(description = "最后执行时间")
    private Date lastExecutedAt;

    @Schema(description = "执行次数")
    private Integer executionCount;

    @Schema(description = "调度表达式")
    private String scheduleExpress;

    @Schema(description = "是否启用")
    private Boolean isEnable;

    @Schema(description = "规则SQL")
    private String ruleSql;

    @Schema(description = "SQL上下文")
    private List<SqlContext> sqlContextList;

    @Schema(description = "数据源编码")
    private String dataSourceCode = "DEFAULT_DS";

    @Schema(description = "运行参数")
    private Map<String,Object> params;


}

