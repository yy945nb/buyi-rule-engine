package com.ymware.engine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.ymware.engine.permission.IgnorePermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 规则引擎工作流配置表
 * </p>
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("rule_engine_flow")
@Schema(description = "RuleEngineFlow对象 - 规则引擎工作流配置表")
@IgnorePermission
public class RuleEngineFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "工作流编码")
    @TableField("code")
    private String code;

    @Schema(description = "工作流名称")
    @TableField("name")
    private String name;

    @Schema(description = "工作流描述")
    @TableField("description")
    private String description;

    @Schema(description = "所属工作空间ID")
    @TableField("workspace_id")
    private Long workspaceId;

    @Schema(description = "所属工作空间编码")
    @TableField("workspace_code")
    private String workspaceCode;

    @Schema(description = "规则引擎DAG流配置JSON")
    @TableField("config_json")
    private String configJson;

    @Schema(description = "发布状态：0未发布，1已发布")
    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}