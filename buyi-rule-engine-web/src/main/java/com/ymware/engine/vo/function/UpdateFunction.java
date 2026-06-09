package com.ymware.engine.vo.function;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/9/11
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateFunction extends AddFunction{

    @NotNull
    private Long id;

}
