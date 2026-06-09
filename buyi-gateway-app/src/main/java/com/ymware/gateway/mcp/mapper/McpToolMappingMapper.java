package com.ymware.gateway.mcp.mapper;

import com.ymware.gateway.mcp.model.McpToolMappingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface McpToolMappingMapper {

    int insert(McpToolMappingDO record);

    int update(McpToolMappingDO record);

    int deleteById(@Param("id") Long id);

    McpToolMappingDO findById(@Param("id") Long id);

    List<McpToolMappingDO> findByServiceId(@Param("serviceId") String serviceId);

    McpToolMappingDO findByServiceIdAndToolName(@Param("serviceId") String serviceId,
                                                 @Param("toolName") String toolName);

    List<McpToolMappingDO> findEnabledByServiceId(@Param("serviceId") String serviceId);

    List<McpToolMappingDO> findAllEnabled();
}
