package com.ymware.gateway.mcp.mapper;

import com.ymware.gateway.mcp.model.McpApiCallLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface McpApiCallLogMapper {

    int insert(McpApiCallLogDO record);

    int insertSimpleLog(@Param("userId") String userId,
                        @Param("serviceId") String serviceId,
                        @Param("requestPath") String requestPath,
                        @Param("statusCode") Integer statusCode,
                        @Param("responseTimeMs") Integer responseTimeMs,
                        @Param("clientIp") String clientIp);
}
