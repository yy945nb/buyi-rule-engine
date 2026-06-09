package com.ymware.engine.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "添加节点请求类")
@Data
@Builder
public class AddExpressionNodeRequest implements Serializable {

    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    private String serviceName;

    /**
     * 调用方式
     */
    @Schema(description = "调用方式")
    private String callMethod;
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
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;
}
