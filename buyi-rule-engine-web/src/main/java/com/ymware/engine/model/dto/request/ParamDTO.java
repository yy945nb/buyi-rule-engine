package com.ymware.engine.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "参数设置类")
public class ParamDTO implements Serializable {

    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paramName;

    @Schema(description = "参数描述")
    private String paramDescription;

    @Schema(description = "参数排序", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer paramSortNum;

    @Schema(description = "参数类型", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = "String,Integer,Long,List", example = "String")
    private String paramDataType;
}
