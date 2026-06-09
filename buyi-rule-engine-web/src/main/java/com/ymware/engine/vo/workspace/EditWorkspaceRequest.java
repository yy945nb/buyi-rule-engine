package com.ymware.engine.vo.workspace;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 〈AddWorkspaceRequest〉
 *
 * @author 丁乾文
 * @date 2021/6/23 5:58 下午
 * @since 1.0.0
 */
@Data
public class EditWorkspaceRequest {

    @NotNull
    private Long id;
    @NotBlank
    private String name;

    private String description;


}
