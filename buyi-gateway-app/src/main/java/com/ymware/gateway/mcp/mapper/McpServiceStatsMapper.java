package com.ymware.gateway.mcp.mapper;

import com.ymware.gateway.mcp.model.McpServiceStatsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface McpServiceStatsMapper {

    int insertOrUpdate(McpServiceStatsDO record);

    McpServiceStatsDO findByServiceIdAndDate(@Param("serviceId") String serviceId,
                                              @Param("dateKey") LocalDate dateKey);

    List<McpServiceStatsDO> findRecentByServiceId(@Param("serviceId") String serviceId,
                                                    @Param("days") int days);

    List<String> findDistinctServiceIds();
}
