package com.ymware.engine.vo.rule.general;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/7/16
 * @since 1.0.0
 */
@Data
public class RunTestRequest {

    /**
     * 规则id
     */
    @NotNull
    private Long id;

    /**
     * DataStatus
     */
    @NotNull
    private Integer status;

    @NotEmpty
    private String workspaceCode;

    @NotEmpty
    private String code;

    private Map<String, Object> param = new HashMap<>();

}
