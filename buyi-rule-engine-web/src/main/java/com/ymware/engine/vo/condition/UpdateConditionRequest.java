package com.ymware.engine.vo.condition;

import lombok.Data;
import jakarta.validation.constraints.Size;

import jakarta.validation.Valid;
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
public class UpdateConditionRequest {
    @NotNull
    private Long id;

    @Size(min = 1, max = 25, message = "条件名称长度在 1 到 25 个字符")
    @NotBlank(message = "条件名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "条件配置不能为空")
    @Valid
    private ConfigBean config;

}
