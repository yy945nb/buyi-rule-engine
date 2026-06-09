package com.ymware.engine.service;

import com.ymware.engine.common.vo.PageRequest;
import com.ymware.engine.common.vo.PageResult;
import com.ymware.engine.vo.operation.record.OperationRecordRequest;
import com.ymware.engine.vo.operation.record.OperationRecordResponse;

/**
 * 〈OperationRecordService〉
 *
 * @author 丁乾文
 * @date 2021/9/9 3:57 下午
 * @since 1.0.0
 */
public interface OperationRecordService {

    /**
     * 保存操作记录
     *
     * @param description 描述
     * @param dataId      id
     * @param dataType    type
     */
    void save(String description, Long dataId, Integer dataType);

    /**
     * 操作记录
     *
     * @param recordRequestPageRequest r
     * @return r
     */
    PageResult<OperationRecordResponse> operationRecord(PageRequest<OperationRecordRequest> recordRequestPageRequest);


    /**
     * 删除操作记录
     *
     * @param id 操作记录id
     * @return true：删除成功
     */
    Boolean delete(Long id);

}
