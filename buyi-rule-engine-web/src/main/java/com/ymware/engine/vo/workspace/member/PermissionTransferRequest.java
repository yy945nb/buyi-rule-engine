package com.ymware.engine.vo.workspace.member;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 〈PermissionTransferRequest〉
 *
 * @author 丁乾文
 * @date 2021/6/23 7:43 下午
 * @since 1.0.0
 */
@Data
public class PermissionTransferRequest {

    @NotNull
    private Long workspaceId;

    @NotNull
    private Long userId;

    @NotNull
    private Integer type;
}
