package com.ymware.engine.executor.impl;

import com.ymware.engine.enums.RemoteInvokeTypeEnums;
import com.ymware.engine.model.node.NodeServiceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 内部服务调用
 */
@Component
@Slf4j
public class InternalServiceInvoke extends HttpRemoteInvoke {


    @Autowired
    @LoadBalanced
    private RestTemplate restTemplate;

    @Override
    public String parseDomain(NodeServiceDto domain) {
        return "http://" + domain.getServiceName();
    }

    @Override
    public RemoteInvokeTypeEnums type() {
        return RemoteInvokeTypeEnums.INTERNAL;
    }

    @Override
    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }
}
