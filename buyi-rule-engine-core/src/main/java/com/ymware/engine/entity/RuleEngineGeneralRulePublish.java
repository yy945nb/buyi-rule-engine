package com.ymware.engine.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RuleEngineGeneralRulePublish implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long generalRuleId;

    private String generalRuleCode;

    private String generalRuleName;

    private Long workspaceId;

    private String workspaceCode;

    private String data;

    /**
     * see RuleStatus
     */
    private Integer status;

    private Integer loadingMode;

    private String valueType;
    /**
     * 版本号
     */
    private String version;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;


}