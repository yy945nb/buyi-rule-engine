package com.ymware.engine.vo.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/***
 * 验证工作空间code
 * @author niuxiangqian
 * @version 1.0
 * @since 2021/7/14 4:26 下午
 **/
@Data
public class VerifyWorkspaceRequest {
    @NotBlank
    @Schema(description = "工作空间code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;
}
