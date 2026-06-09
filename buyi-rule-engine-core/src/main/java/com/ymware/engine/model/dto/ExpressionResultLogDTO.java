package com.ymware.engine.model.dto;

import com.ymware.engine.model.FunctionApiModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表达式结果实体
 */
@Data
public class ExpressionResultLogDTO {

    /**
     * 执行器编号
     */
    private Long executorId;

    /**
     * 结果类型 : 表达式、函数
     */
    private String resultType;

    /**
     * 表达式编号
     */
    private Long expressionConfigId;

    /**
     * 表达式
     */
    private String expression;

    /**
     * 函数信息
     */
    private FunctionApiModel functionApiModel;

    /**
     * 调试追踪内容
     */
    private Map<String, Object> debugTraceContent;

    /**
     * 函数参数
     */
    private List<Object> funcArgs;

    /**
     * 函数结果
     */
    private Object result;

    /**
     * 描述
     */
    private String description;

}
