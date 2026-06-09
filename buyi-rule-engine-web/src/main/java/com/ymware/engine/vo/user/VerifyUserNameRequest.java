package com.ymware.engine.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/***
 * 验证用户是否可用request
 * @author niuxiangqian
 * @version 1.0
 * @since 2021/7/14 2:54 下午
 **/
@Data
public class VerifyUserNameRequest {
    @Size(min = 2, max = 10, message = "用户名需要2到10位")
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
}
