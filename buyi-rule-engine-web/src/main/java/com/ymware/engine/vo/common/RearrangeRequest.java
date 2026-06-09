package com.ymware.engine.vo.common;

import lombok.Data;

import jakarta.validation.constraints.NotNull;


/**
 * @author Administrator
 */
@Data
public class RearrangeRequest {

    @NotNull
    private Long id;
    @NotNull
    private Integer orderNo;

}
