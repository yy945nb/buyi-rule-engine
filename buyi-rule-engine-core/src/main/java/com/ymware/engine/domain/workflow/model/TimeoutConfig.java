package com.ymware.engine.domain.workflow.model;

import lombok.Data;

/**
 * 超时配置 - 工作流节点共享模型
 */
@Data
public class TimeoutConfig {
    private int timeout;
    private int retryTimes;
}
