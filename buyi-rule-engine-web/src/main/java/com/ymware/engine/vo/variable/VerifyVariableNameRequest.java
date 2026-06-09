package com.ymware.engine.vo.variable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotBlank;

/***
 * 验证变量名字
 * @author niuxiangqian
 * @version 1.0
 * @since 2021/7/15 4:11 下午
 **/
@Data
public class VerifyVariableNameRequest {

    @NotBlank
    @Size(min = 1, max = 25, message = "变量名称长度在 1 到 25 个字符")
    private String name;


}
