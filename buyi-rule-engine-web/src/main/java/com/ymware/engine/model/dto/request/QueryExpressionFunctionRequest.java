package com.ymware.engine.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "查询函数请求类")
public class QueryExpressionFunctionRequest implements Serializable {
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

}
