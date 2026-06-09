package com.ymware.engine.domain.workflow.model;

import lombok.Data;

/**
 * API配置 - 工作流HTTP节点共享模型
 */
@Data
public class ApiConfig {
    private String method;
    private Object url;
}
