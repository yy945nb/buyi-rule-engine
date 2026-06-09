package com.ymware.engine.compute.api.config;

import com.ymware.engine.compute.api.RemoteExpressionConfigService;
import com.ymware.engine.compute.api.RemoteHttpService;
import com.ymware.engine.compute.enums.ExpressionConfigCallEnum;
import com.ymware.engine.model.request.ConfigDiscoverRequest;
import com.ymware.engine.result.ExpressionConfigInfo;
import com.ymware.engine.consts.ExpressionConstants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 远端获取配置
 * 从服务端中获取相应的配置信息
 */
public class HttpExpressionConfigService implements RemoteExpressionConfigService {

    @Autowired
    private RemoteHttpService remoteHttpService;

    @Override
    public String configKey() {
        return ExpressionConfigCallEnum.http.name();
    }

    @Override
    public ExpressionConfigInfo getConfigInfo(String serviceName, String businessCode, String executorCode) {
        ConfigDiscoverRequest request = new ConfigDiscoverRequest();
        request.setServiceName(serviceName);
        request.setBusinessCode(businessCode);
        request.setExecutorCode(executorCode);
        return remoteHttpService.call(ExpressionConstants.ENGINE_SERVER_ID, ExpressionConstants.SERVER_CONFIG_DISCOVERY, request, ExpressionConfigInfo.class);
    }


}
