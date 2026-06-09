package com.ymware.engine.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ymware.engine.entity.ExpressionTraceLogInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "执行器的表达式配置响应类")
@Data
public class ExpressionExecutorDetailConfigDTO implements Serializable {
    /**
     * 主键id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "表达式id")
    private Long id;

    /**
     * 表达式类型:condition 条件表达式;rule 规则表达式
     */
    @Schema(description = "表达式类型", allowableValues = "condition,rule")
    private String expressionType;

    /**
     * 执行器id
     */
    @Schema(description = "执行器id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long executorId;

    @Schema(description = "上级编号")
    private Long parentId;
    /**
     * 表达式编码
     */
    @Schema(description = "表达式编码")
    private String expressionCode;
    /**
     * 表达式标题
     */
    @Schema(description = "表达式标题")
    private String expressionTitle;
    /**
     * 表达式描述
     */
    @Schema(description = "表达式描述")
    private String expressionDescription;
    /**
     * 表达式内容
     */
    @Schema(description = "表达式内容")
    private String expressionContent;

    /**
     * 表达式状态:1 启用,0禁用
     */
    @Schema(description = "表达式状态")
    private Integer expressionStatus;

    /**
     * 配置能力开关
     */
    @Schema(description = "能力开关")
    private String configurabilityJson;

    @Schema(description = "优先级顺序")
    private Integer priorityOrder;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime updateTime;

    private List<ExpressionTraceLogInfo> traceLogInfos;

    /**
     * 最近没有命中过
     */
    private Boolean lastMissed;
}
