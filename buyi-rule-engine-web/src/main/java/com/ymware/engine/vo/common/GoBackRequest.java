package com.ymware.engine.vo.common;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * @author Administrator
 */
@Data
public class GoBackRequest {

    /**
     * 规则、决策表、规则集
     */
    @NotNull
    private Long dataId;

    /**
     * 回退到哪个版本
     */
    @NotEmpty
    private String version;

}
