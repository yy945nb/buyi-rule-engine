package com.ymware.engine.vo.parameter;

import lombok.Data;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/8/25
 * @since 1.0.0
 */
@Data
public class UpdateInputParameterRequest {

    @NotNull
    private Long id;

    @NotBlank(message = "规则参数名称不能为空")
    @Size(min = 1, max = 25, message = "规则参数名称长度在 1 到 25 个字符")
    private String name;

    private String description;

}
