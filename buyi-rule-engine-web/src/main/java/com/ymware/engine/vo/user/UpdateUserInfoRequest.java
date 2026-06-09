package com.ymware.engine.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/4/5
 * @since 1.0.0
 */
@Data
public class UpdateUserInfoRequest {

    @NotNull
    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "用户性别")
    private String sex;


    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "用户手机号")
    private Long phone;

    @Email
    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "个人描述")
    private String description;

}
