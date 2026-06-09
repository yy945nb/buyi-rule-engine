package com.ymware.engine.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 丁乾文
 * @date 2019/8/23
 * @since 1.0.0
 */
@Data
@Schema(description = "获取验证码请求参数")
public class GetVerifyCodeByEmailRequest {

    @NotNull
    @Schema(description = "获取验证码用途类型")
    private Integer type;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    @Schema(description = "邮箱")
    private String email;
}
