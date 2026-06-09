package com.ymware.engine.vo.variable;

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
public class UpdateVariableRequest {

    @NotNull
    private Long id;

    @Size(min = 1, max = 25, message = "变量名称长度在 1 到 25 个字符")
    @NotBlank
    private String name;

    @NotNull
    private Integer type;

    private String description;

    @NotNull
    private String value;

    /**
     * 函数
     */
    @NotNull
    private VariableFunction function;


}
