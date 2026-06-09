package com.ymware.gateway.mcp.mapper;

import com.ymware.gateway.mcp.model.McpServiceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface McpServiceMapper {

    int insert(McpServiceDO record);

    int update(McpServiceDO record);

    int deleteById(@Param("id") Long id);

    McpServiceDO findById(@Param("id") Long id);

    McpServiceDO findByServiceId(@Param("serviceId") String serviceId);

    List<McpServiceDO> findByStatus(@Param("status") String status);

    List<McpServiceDO> findByNacosServiceId(@Param("nacosServiceId") String nacosServiceId);

    List<McpServiceDO> findByConditions(@Param("status") String status,
                                         @Param("name") String name,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    int countByConditions(@Param("status") String status, @Param("name") String name);

    List<McpServiceDO> findAll();
}
