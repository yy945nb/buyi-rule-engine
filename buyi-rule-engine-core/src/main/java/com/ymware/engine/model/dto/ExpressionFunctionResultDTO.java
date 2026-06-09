package com.ymware.engine.model.dto;

import com.ymware.engine.model.FunctionApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 表达式函数结果数据转换层
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExpressionFunctionResultDTO extends ExpressionResultLogDTO {

    /**
     * 函数信息
     */
    private FunctionApiModel functionApiModel;

    /**
     * 函数参数
     */
    private List<Object> funcArgs;
}
