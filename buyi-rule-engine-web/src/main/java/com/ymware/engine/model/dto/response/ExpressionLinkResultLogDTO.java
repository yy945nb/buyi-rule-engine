package com.ymware.engine.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "回调成功日志响应类")
@Data
public class ExpressionLinkResultLogDTO implements Serializable {

    /**
     * 主键id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键id")
    private Long id;

    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    private String serviceName;

    /**
     * 阶段类型
     */
    @Schema(description = "阶段类型")
    private String stageType;

    /**
     * 执行链路编号
     */
    @Schema(description = "执行链路编号")
    private String linkNo;

    /**
     * 业务编码
     */
    @Schema(description = "业务编码")
    private String businessCode;

    /**
     * 事件编码
     */
    @Schema(description = "服务名称")
    private String eventCode;

    /**
     * 唯一编号(负责确定唯一编号,类似userId)
     */
    @Schema(description = "唯一编号")
    private String uniqueNo;

    /**
     * 执行结果描述
     */
    @Schema(description = "执行结果描述")
    private String resultDescription;
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
