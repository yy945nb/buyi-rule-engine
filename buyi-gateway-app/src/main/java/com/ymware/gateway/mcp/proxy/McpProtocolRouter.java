package com.ymware.gateway.mcp.proxy;

import com.ymware.gateway.mcp.discovery.McpServiceInfo;
import org.springframework.stereotype.Component;

@Component
public class McpProtocolRouter {

    public McpRouteDecision decide(String serviceId, McpServiceInfo service) {
        if (service == null) {
            return McpRouteDecision.notFound(serviceId);
        }
        if (!service.isActive()) {
            return McpRouteDecision.inactive(serviceId, service);
        }
        if (service.getServiceType() == McpServiceInfo.ServiceType.TRANSPARENT) {
            return McpRouteDecision.transparent(service);
        }
        return McpRouteDecision.protocolParse(service);
    }

    public record McpRouteDecision(
            RouteType routeType,
            McpServiceInfo service,
            String errorMessage
    ) {
        public enum RouteType { TRANSPARENT, PROTOCOL_PARSE, NOT_FOUND, INACTIVE }

        static McpRouteDecision transparent(McpServiceInfo service) {
            return new McpRouteDecision(RouteType.TRANSPARENT, service, null);
        }

        static McpRouteDecision protocolParse(McpServiceInfo service) {
            return new McpRouteDecision(RouteType.PROTOCOL_PARSE, service, null);
        }

        static McpRouteDecision notFound(String serviceId) {
            return new McpRouteDecision(RouteType.NOT_FOUND, null, "MCP service not found: " + serviceId);
        }

        static McpRouteDecision inactive(String serviceId, McpServiceInfo service) {
            return new McpRouteDecision(RouteType.INACTIVE, service, "MCP service is not active: " + serviceId);
        }
    }
}
