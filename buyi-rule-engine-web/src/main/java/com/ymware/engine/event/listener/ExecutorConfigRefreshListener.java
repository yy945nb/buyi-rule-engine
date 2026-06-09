package com.ymware.engine.domain.rule.event.listener;

import com.ymware.engine.event.ExecutorConfigRefreshEvent;
import com.ymware.engine.executor.ExpressionExecutor;
import com.ymware.engine.entity.ExpressionExecutorBaseInfo;
import com.ymware.engine.service.ExpressionExecutorConfigService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 执行器刷新配置
 */
@Component
public class ExecutorConfigRefreshListener implements ApplicationListener<ExecutorConfigRefreshEvent> {
    private final Logger log = getLogger(ExecutorConfigRefreshListener.class);
    @Autowired
    private ExpressionExecutor expressionExecutor;

    @Autowired
    private ExpressionExecutorConfigService expressionExecutorConfigService;

    @Override
    public void onApplicationEvent(ExecutorConfigRefreshEvent event) {

        Long executorId = (Long) event.getSource();

        log.info("trigger event refresh ExecutorConfigRefreshEvent -> {}", executorId);

        ExpressionExecutorBaseInfo baseInfo = expressionExecutorConfigService.getById(executorId);

        // 重新构建业务数据
        expressionExecutor.getRefreshBusinessConfigInfo(baseInfo.getServiceName(), baseInfo.getBusinessCode(), baseInfo.getExecutorCode());
    }

}
