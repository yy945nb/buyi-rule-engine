package com.ymware.engine.vo.workspace.member;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 〈DeleteMemberRequest〉
 *
 * @author 丁乾文
 * @date 2021/6/23 5:30 下午
 * @since 1.0.0
 */
@Data
public class DeleteMemberRequest {

    @NotNull
    private Long workspaceId;

    @NotNull
    private Long userId;

}
