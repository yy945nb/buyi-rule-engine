package com.ymware.engine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2021-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RuleEngineOperationRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private Long workspaceId;

    private String workspaceCode;

    private String description;

    private Integer dataType;

    private Long dataId;

    private Date operationTime;

}
