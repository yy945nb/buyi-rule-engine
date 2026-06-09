package com.ymware.engine.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "引擎表达式执行器响应类")
@Data
public class ExpressionExecutorBaseDTO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键id")
    private Long id;

    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    private String serviceName;

    /**
     * 业务编码
     */
    @Schema(description = "业务编码")
    private String businessCode;

    /**
     * 执行器名称
     */
    @Schema(description = "执行器名称")
    private String executorCode;
    /**
     * 执行器描述
     */
    @Schema(description = "执行器描述")
    private String executorDescription;
    /**
     * 配置能力开关
     */
    @Schema(description = "能力开关")
    private String configurabilityJson;

    /**
     * 变量定义,方便索引
     */
    @Schema(description = "变量定义")
    private String varDefinition;

    /**
     * 是否已删除:0否,1.是
     */
    @Schema(description = "是否已删除")
    private Boolean deleted;

    /**
     * 执行器状态:0.创建，1.启用，2.禁用
     */
    @Schema(description = "执行器状态")
    private Integer status;

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
}
