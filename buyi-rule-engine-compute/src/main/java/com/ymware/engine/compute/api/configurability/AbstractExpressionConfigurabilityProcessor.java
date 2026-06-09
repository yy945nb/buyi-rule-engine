package com.ymware.engine.compute.api.configurability;

import com.ymware.engine.compute.api.ExpressionExecutorFilter;
import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.enums.ExpressionConfigurabilitySwitchEnum;
import com.ymware.engine.compute.helper.ConfigurabilityHelper;
import com.ymware.engine.compute.process.ExpressionFilterChain;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.result.ExpressionConfigInfo;
import com.ymware.engine.model.ExpressionConfigTreeModel;

import java.util.Map;

/**
 * 抽象的配置能力构建
 */
public abstract class AbstractExpressionConfigurabilityProcessor implements ExpressionExecutorFilter {

    /**
     * 执行具体能力
     *
     * @param envContext
     * @param baseRequest
     * @param configInfo
     */
    public abstract Object configurabilityExecutor(ExpressionEnvContext envContext, ExpressionBaseRequest baseRequest, ExpressionConfigInfo configInfo, ExpressionConfigTreeModel configTreeModel, ExpressionFilterChain chain);

    protected boolean switchOn(ExpressionConfigTreeModel configInfo) {
        final Map<String, Object> configurabilityMap = configInfo.getConfigurabilityMap();
        return ConfigurabilityHelper.isEnableExpressionConfigurability(configurabilityMap, configurabilityKey());
    }

    @Override
    public Object doExpressionFilter(ExpressionEnvContext env, ExpressionConfigInfo configInfo, ExpressionConfigTreeModel configTreeModel, ExpressionBaseRequest request, ExpressionFilterChain chain) {
        if (switchOn(configTreeModel)) {
            return configurabilityExecutor(env, request, configInfo, configTreeModel, chain);
        } else {
            return chain.doFilter(env, configInfo, configTreeModel, request);
        }
    }

    public abstract ExpressionConfigurabilitySwitchEnum configurabilityKey();
}
