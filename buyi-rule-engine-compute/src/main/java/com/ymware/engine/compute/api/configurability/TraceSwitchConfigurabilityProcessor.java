package com.ymware.engine.compute.api.configurability;

import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.enums.ExecutorConfigurabilitySwitchEnum;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.result.ExpressionConfigInfo;

/**
 * 动态开关追踪能力
 *
 * @author liukaixiong
 * @date 2024/9/11 - 11:36
 */
public class TraceSwitchConfigurabilityProcessor extends AbstractExecutorConfigurabilityProcessor {

    @Override
    public void configurabilityExecutor(ExpressionEnvContext envContext, ExpressionBaseRequest baseRequest, ExpressionConfigInfo configInfo) {
        // 开启追踪能力
        envContext.enableTrace();
    }

    @Override
    public ExecutorConfigurabilitySwitchEnum configurabilityKey() {
        return ExecutorConfigurabilitySwitchEnum.enableTrace;
    }
}
