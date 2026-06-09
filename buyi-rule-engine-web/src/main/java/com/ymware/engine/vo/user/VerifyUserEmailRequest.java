package com.ymware.engine.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/***
 * 注册邮箱检查
 * @author niuxiangqian
 * @version 1.0
 * @since 2021/7/14 3:08 下午
 **/
@Data
public class VerifyUserEmailRequest {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}
