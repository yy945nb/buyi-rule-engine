package com.ymware.engine.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "添加注册变量请求类")
public class AddExpressionVariableRequest implements Serializable {

    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    private String serviceName;

    /**
     * 变量编码
     */
    @Schema(description = "变量编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String varCode;

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
     * 状态:0.启用,1.禁用
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

}
