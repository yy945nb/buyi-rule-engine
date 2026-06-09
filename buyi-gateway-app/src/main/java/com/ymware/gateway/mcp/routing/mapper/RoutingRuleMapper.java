package com.ymware.gateway.mcp.routing.mapper;

import com.ymware.gateway.mcp.routing.model.RoutingRuleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoutingRuleMapper {

    int insert(RoutingRuleDO record);

    int update(RoutingRuleDO record);

    int deleteById(@Param("id") Long id);

    RoutingRuleDO findById(@Param("id") Long id);

    List<RoutingRuleDO> findAllEnabled();

    List<RoutingRuleDO> findByConditions(@Param("enabled") Boolean enabled,
                                         @Param("keyword") String keyword,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    int countByConditions(@Param("enabled") Boolean enabled, @Param("keyword") String keyword);
}
