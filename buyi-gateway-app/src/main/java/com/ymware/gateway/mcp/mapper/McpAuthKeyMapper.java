package com.ymware.gateway.mcp.mapper;

import com.ymware.gateway.mcp.model.McpAuthKeyDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface McpAuthKeyMapper {

    int insert(McpAuthKeyDO record);

    int update(McpAuthKeyDO record);

    int deleteById(@Param("id") Long id);

    McpAuthKeyDO findById(@Param("id") Long id);

    McpAuthKeyDO findByKeyHash(@Param("keyHash") String keyHash);

    List<McpAuthKeyDO> findByUserId(@Param("userId") String userId);

    List<McpAuthKeyDO> findByServiceId(@Param("serviceId") String serviceId);

    McpAuthKeyDO findByUserIdAndServiceId(@Param("userId") String userId,
                                           @Param("serviceId") String serviceId);

    List<McpAuthKeyDO> findByConditions(@Param("userId") String userId,
                                         @Param("serviceId") String serviceId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    int countByConditions(@Param("userId") String userId, @Param("serviceId") String serviceId);

    int updateLastUsedTime(@Param("id") Long id, @Param("lastUsedAt") LocalDateTime lastUsedAt);

    int deactivateUserServiceKeys(@Param("userId") String userId, @Param("serviceId") String serviceId);
}
