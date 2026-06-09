package com.ymware.engine.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author dqw
 * @since 2020-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RuleEngineGeneralRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private Long ruleId;

    private String description;

    private Long workspaceId;

    private String workspaceCode;

    private Long createUserId;

    /**
     * 当前规则的最新状态
     */
    private Integer status;
    /**
     * 当前最新的版本号
     */
    private String currentVersion;
    /**
     * 当前线上的版本号
     */
    private String publishVersion;

    private Integer enableDefaultAction;

    private String defaultActionValue;

    private Integer defaultActionType;

    private String defaultActionValueType;

    /**
     * 注意，规则模拟运行不会触发
     * <p>
     * AbnormalAlarm
     */
    private String abnormalAlarm;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;


}
