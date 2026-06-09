package com.ymware.engine.vo.workspace;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 〈UpdateAccessKeyRequest〉
 *
 * @author 丁乾文
 * @date 2021/6/25 4:43 下午
 * @since 1.0.0
 */
@Data
public class UpdateAccessKeyRequest {

    @NotNull
    private Long id;

    @NotBlank
    private String accessKeyId;

    @NotBlank
    private String accessKeySecret;

}
