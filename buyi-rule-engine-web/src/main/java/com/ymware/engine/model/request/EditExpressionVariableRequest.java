package com.ymware.engine.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "编辑注册变量请求类")
@Data
public class EditExpressionVariableRequest implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
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
     * 状态:0.启用,1.禁用
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;
}
