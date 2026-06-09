package com.ymware.engine.compute.api.config;

import com.ymware.engine.compute.api.RemoteExpressionConfigService;
import com.ymware.engine.compute.config.props.ExpressionProperties;
import com.ymware.engine.result.ExpressionConfigInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表达式配置调用管理
 */
public class ExpressionConfigCallManager implements InitializingBean {

    @Autowired
    private List<RemoteExpressionConfigService> remoteExpressionConfigServiceList;

    private Map<String, RemoteExpressionConfigService> keyMap;

    @Autowired
    private ExpressionProperties expressionProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.keyMap = remoteExpressionConfigServiceList.stream().collect(Collectors.toMap(RemoteExpressionConfigService::configKey, i -> i));
    }

    public ExpressionConfigInfo getConfigInfo(String serviceName, String businessCode, String executorCode) {
        return keyMap.get(expressionProperties.getExpressionConfigCall()).getConfigInfo(serviceName, businessCode, executorCode);
    }


}
