package com.ymware.gateway.core.service;

import com.ymware.gateway.core.capability.CapabilityChecker;
import com.ymware.gateway.core.protocol.ProtocolAdapter;
import com.ymware.gateway.core.resilience.FailoverStrategy;
import com.ymware.gateway.core.router.ModelRouter;
import com.ymware.gateway.core.stats.ActiveRequestTracker;
import com.ymware.gateway.core.stats.RequestStatsCollector;
import com.ymware.gateway.core.stats.RequestStatsContext;
import com.ymware.gateway.provider.ProviderClient;
import com.ymware.gateway.provider.ProviderClientFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Rerank 网关服务，委托 AbstractGatewayService.executeNonStreaming 编排。
 */
@Service
public class RerankGatewayService extends AbstractGatewayService {

    public RerankGatewayService(ModelRouter modelRouter, CapabilityChecker capabilityChecker,
                                 ProviderClientFactory providerClientFactory,
                                 RequestStatsCollector requestStatsCollector,
                                 FailoverStrategy failoverStrategy,
                                 ActiveRequestTracker activeRequestTracker) {
        super(modelRouter, capabilityChecker, providerClientFactory,
              requestStatsCollector, failoverStrategy, activeRequestTracker);
    }

    public Mono<?> rerankWithStats(Object rawRequest, ProtocolAdapter adapter, RequestStatsContext context) {
        return executeNonStreaming(rawRequest, adapter, context, ProviderClient::rerank);
    }
}
