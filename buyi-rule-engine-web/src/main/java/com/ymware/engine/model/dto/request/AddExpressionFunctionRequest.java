package com.ymware.engine.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Schema(description = "添加规则引擎函数请求类")
@Data
public class AddExpressionFunctionRequest implements Serializable {

    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    private String serviceName;

    /**
     * 函数名称
     */
    @Schema(description = "函数名称")
    private String funcName;

    /**
     * 函数描述
     */
    @Schema(description = "函数描述")
    private String funcDescription;

    /**
     * 函数入参json说明
     */
    @Schema(description = "函数入参json说明")
    private List<ParamDTO> bodyParam;

    /**
     * 公共入参
     */
    @Schema(description = "公共入参")
    private List<ParamDTO> commonParam;

    /**
     * 状态:0启用，1禁用
     */
    @Schema(description = "函数状态")
    private Integer status;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

}
