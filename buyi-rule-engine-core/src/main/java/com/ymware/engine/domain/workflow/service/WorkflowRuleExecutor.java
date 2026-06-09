package com.ymware.engine.domain.workflow.service;

import java.util.Map;

/**
 * 规则执行桥接接口，用于打通工作流引擎与传统规则引擎的执行能力
 */
public interface WorkflowRuleExecutor {

    /**
     * 执行规则引擎流程或规则
     *
     * @param code          规则编码或流程编码
     * @param workspaceCode 工作空间编码
     * @param variables     输入变量
     * @return 执行结果Map
     */
    Map<String, Object> execute(String code, String workspaceCode, Map<String, Object> variables);
}
