package com.ymware.engine.compute.api.configurability;

import com.ymware.engine.compute.api.ExpressionExecutorPostProcessor;
import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.enums.ExecutorConfigurabilitySwitchEnum;
import com.ymware.engine.compute.helper.ConfigurabilityHelper;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.result.ExpressionConfigInfo;

import java.util.Map;

/**
 * 抽象的配置能力构建
 */
public abstract class AbstractExecutorConfigurabilityProcessor implements ExpressionExecutorPostProcessor {

    /**
     * 前置处理器
     *
     * @param envContext
     * @param baseRequest
     * @param configInfo
     */
    @Override
    public void beforeExecutor(ExpressionEnvContext envContext, ExpressionBaseRequest baseRequest, ExpressionConfigInfo configInfo) {
        if (switchOn(configInfo)) {
            configurabilityExecutor(envContext, baseRequest, configInfo);
        }
    }

    /**
     * 执行具体能力
     *
     * @param envContext
     * @param baseRequest
     * @param configInfo
     */
    public abstract void configurabilityExecutor(ExpressionEnvContext envContext, ExpressionBaseRequest baseRequest, ExpressionConfigInfo configInfo);

    protected boolean switchOn(ExpressionConfigInfo configInfo) {
        final Map<String, Object> configurabilityMap = configInfo.getConfigurabilityMap();
        return ConfigurabilityHelper.isEnableExecutorConfigurability(configurabilityMap, configurabilityKey());
    }

    /**
     * 后置处理器
     *
     * @param envContext
     * @param baseRequest
     * @param configTreeModelList
     */
    @Override
    public void afterExecutor(ExpressionEnvContext envContext, ExpressionBaseRequest baseRequest, ExpressionConfigInfo configTreeModelList) {

    }

    public abstract ExecutorConfigurabilitySwitchEnum configurabilityKey();
}
