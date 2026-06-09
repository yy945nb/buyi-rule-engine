package com.ymware.engine.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "规则引擎函数响应类")
public class ExpressionFunctionDTO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "函数id")
    private Long id;

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
    private String bodyParam;

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
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime updateTime;
}
