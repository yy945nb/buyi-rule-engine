package com.ymware.engine.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "查询服务节点请求类")
public class QueryExpressionNodeRequest implements Serializable {
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
     * 路由地址
     */
    @Schema(description = "路由地址")
    private String domain;

    /**
     * 状态:0.启用1.禁用
     */
    @Schema(description = "状态")
    private Integer status;
}
