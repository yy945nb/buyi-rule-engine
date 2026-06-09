package com.ymware.engine.executor;

import com.ymware.engine.testrun.input.TaskRunInput;
import com.ymware.engine.testrun.model.TaskInfo;

/**
 * 异步工作流执行器接口
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/08/22 16:10
 */
public interface AsyncWorkflowExecutor {

    /**
     * 异步执行工作流
     *
     * @param taskId 任务ID
     * @param taskInfo 任务信息
     * @param input 工作流输入参数
     */
    void executeAsync(String taskId, TaskInfo taskInfo, TaskRunInput input);
}

