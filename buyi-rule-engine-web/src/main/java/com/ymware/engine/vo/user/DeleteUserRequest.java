package com.ymware.engine.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 删除用户
 *
 * @author : zhj
 * @date : 2021/6/23 21:46
 **/
@Data
public class DeleteUserRequest {

    @Schema(description = "用户id")
    @NotNull
    private Long id;

}
