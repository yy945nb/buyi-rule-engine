package com.ymware.engine.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "添加全局日志请求类")
public class AddGlobalTraceLogRequest implements Serializable {
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
    @Schema(description = "事件编码")
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
     * 是否执行成功:0.否,1.是
     */
    @Schema(description = "执行链路编号")
    private Boolean executeSuccess;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;


}
