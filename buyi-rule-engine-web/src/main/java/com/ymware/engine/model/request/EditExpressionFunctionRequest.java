package com.ymware.engine.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "编辑规则引擎函数请求类")
@Data
public class EditExpressionFunctionRequest implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "函数id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    private String serviceName;

    /**
     * 函数描述
     */
    @Schema(description = "函数描述")
    private String funcDescription;

    /**
     * 函数入参json说明
     */
    @Schema(description = "函数入参json说明")
    private String bodyParam;
    /**
     * 函数描述
     */
    @Schema(description = "函数描述")
    private String paramDoc;
    /**
     * 公共入参
     */
    @Schema(description = "公共入参")
    private String commonParam;

    /**
     * 状态:0启用，1禁用
     */
    @Schema(description = "函数状态")
    private Integer status;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;

}
