package com.ymware.gateway.mcp.routing.mapper;

import com.ymware.gateway.mcp.routing.model.ServiceCapabilityDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceCapabilityMapper {

    int insert(ServiceCapabilityDO record);

    int update(ServiceCapabilityDO record);

    int deleteById(@Param("id") Long id);

    ServiceCapabilityDO findById(@Param("id") Long id);

    List<ServiceCapabilityDO> findByServiceId(@Param("serviceId") String serviceId);

    List<ServiceCapabilityDO> findByCapabilityTag(@Param("capabilityTag") String capabilityTag);

    List<ServiceCapabilityDO> findAll();

    int updateHealthStatus(@Param("serviceId") String serviceId,
                           @Param("capabilityTag") String capabilityTag,
                           @Param("healthStatus") Boolean healthStatus);

    int updateAvgResponseTime(@Param("serviceId") String serviceId,
                              @Param("capabilityTag") String capabilityTag,
                              @Param("avgResponseTimeMs") Long avgResponseTimeMs);
}
