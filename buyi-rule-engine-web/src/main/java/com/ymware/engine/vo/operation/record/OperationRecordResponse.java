package com.ymware.engine.vo.operation.record;

import lombok.Data;

import java.util.Date;

/**
 * 〈OperationRecordResponse〉
 *
 * @author 丁乾文
 * @date 2021/9/9 5:26 下午
 * @since 1.0.0
 */
@Data
public class OperationRecordResponse {
    private Long id;

    private Long userId;

    private String username;

    private String userAvatar;

    private Long workspaceId;

    private String workspaceCode;

    private String description;

    private Integer dataType;

    private Long dataId;

    private Date operationTime;
}
