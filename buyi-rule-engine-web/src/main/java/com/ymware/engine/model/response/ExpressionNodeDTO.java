package com.ymware.engine.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "表达式服务节点响应类")
public class ExpressionNodeDTO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "节点id")
    private Long id;

    /**
     * 调用方式
     */
    @Schema(description = "调用方式")
    private String callMethod;

    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    private String serviceName;

    /**
     * 服务描述
     */
    @Schema(description = "服务描述")
    private String serviceDescription;

    /**
     * 路由地址
     */
    @Schema(description = "路由地址")
    private String domain;

    /**
     * 状态:0.启用1.禁用
     */
    @Schema(description = "状态")
    private Integer status;

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
}
