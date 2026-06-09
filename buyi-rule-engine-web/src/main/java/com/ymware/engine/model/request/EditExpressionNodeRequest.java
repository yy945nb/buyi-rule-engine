package com.ymware.engine.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "修改服务节点请求类")
@Data
@Builder
public class EditExpressionNodeRequest implements Serializable {
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
    @Schema(description = "服务地址")
    private String domain;

    /**
     * 状态:0.启用1.禁用
     */
    @Schema(description = "状态")
    private Integer status;


    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;
}
