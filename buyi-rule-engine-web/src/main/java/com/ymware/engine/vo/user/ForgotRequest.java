package com.ymware.engine.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 丁乾文
 * @date 2019/8/23
 * @since 1.0.0
 */
@Data
@Schema(description = "用户修改密码请求参数")
public class ForgotRequest {

    @Size(min = 6, max = 16, message = "密码需要6到16位")
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    @Schema(description = "邮箱")
    private String email;

    @NotNull(message = "邮箱验证码不能为空")
    @Schema(description = "邮箱验证码")
    private Integer code;

}
