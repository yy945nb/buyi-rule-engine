package com.ymware.engine.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "表达式注册变量响应类")
public class ExpressionVariableDTO implements Serializable {

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
