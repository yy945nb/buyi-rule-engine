package com.ymware.engine.vo.variable;

import com.ymware.engine.domain.value.model.ParamValue;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 〈VariableFunction〉
 *
 * @author 丁乾文
 * @date 2021/8/2 7:07 下午
 * @since 1.0.0
 */
@Data
public class VariableFunction {

    private Long id;

    private String name;
    /**
     * 函数中所有的参数
     */
    @Valid
    @NotNull
    private List<ParamValue> paramValues;

    private String returnValueType;

}
