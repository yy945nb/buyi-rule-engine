package com.ymware.engine.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "")
@Data
public class QueryExpressionVariableRequest implements Serializable {
    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    private String serviceName;

    /**
     * 变量编码
     */
    @Schema(description = "变量编码")
    private String varCode;

    /**
     * 变量英文名
     */
    @Schema(description = "变量英文名")
    private String varName;

    /**
     * 变量值
     */
    @Schema(description = "变量值")
    private String varValue;

    /**
     * 变量描述
     */
    @Schema(description = "变量描述")
    private String varDescription;

    /**
     * 变量来源:local本地,remote远程
     */
    @Schema(description = "变量来源")
    private String varSource;

    /**
     * 变量数据类型
     */
    @Schema(description = "变量数据类型")
    private String varDataType;

    /**
     * 状态:1.启用,0.禁用
     */
    @Schema(description = "状态")
    private Integer status;
}
