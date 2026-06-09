package com.ymware.engine.vo.rule.general;

import lombok.Data;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/8/28
 * @since 1.0.0
 */
@Data
public class GeneralRuleDefinition {

    private Long id;

    @NotBlank
    @Size(min = 1, max = 25, message = "规则名称长度在 1 到 25 个字符")
    private String name;

    @NotBlank
    @Size(min = 1, max = 25, message = "规则编码长度在 1 到 25 个字符")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_&#\\-]*$", message = "规则Code只能字母开头，以及字母数字_&#-组成")
    private String code;

    private String description;


}
