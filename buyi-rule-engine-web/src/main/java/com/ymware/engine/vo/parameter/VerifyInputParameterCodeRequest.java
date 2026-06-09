package com.ymware.engine.vo.parameter;

import lombok.Data;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/***
 * 验证参数code
 * @author niuxiangqian
 * @version 1.0
 * @since 2021/7/15 4:11 下午
 **/
@Data
public class VerifyInputParameterCodeRequest {

    @Size(min = 1, max = 25, message = "规则参数Code长度在 1 到 25 个字符")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_&#\\-]*$", message = "规则参数Code只能字母开头，以及字母数字_&#-组成")
    @NotBlank(message = "规则参数编码不能为空")
    private String code;

}
