package com.ymware.engine.service;

import com.ymware.engine.common.vo.PageRequest;
import com.ymware.engine.common.vo.PageResult;
import com.ymware.engine.vo.system.log.GetLogResponse;
import com.ymware.engine.vo.system.log.ListLogRequest;
import com.ymware.engine.vo.system.log.ListLogResponse;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2021/3/2
 * @since 1.0.0
 */
public interface SystemLogService {

    /**
     * 查询日志列表
     *
     * @param pageRequest 分页参数
     * @return list
     */
    PageResult<ListLogResponse> list(PageRequest<ListLogRequest> pageRequest);

    /**
     * 根据id删除日志，只能由管理删除
     *
     * @param id 日志id
     * @return true
     */
    Boolean delete(Long id);

    /**
     * 根据id查询日志详情
     *
     * @param id 日志id
     * @return info
     */
    GetLogResponse get(Long id);

}
