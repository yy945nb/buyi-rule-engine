package com.ymware.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ymware.engine.entity.ExpressionLinkResultLog;
import com.ymware.engine.model.dto.request.AddLinkResultLogRequest;
import com.ymware.engine.model.dto.request.DeleteByIdListRequest;
import com.ymware.engine.model.request.EditLinkResultLogRequest;
import com.ymware.engine.model.dto.request.QueryLinkResultLogRequest;
import com.ymware.engine.model.dto.response.ExpressionLinkResultLogDTO;
import com.ymware.engine.model.response.RestResult;

import java.util.List;

/**
 * <p>
 * 成功回调日志表,记录执行完成的日志记录,全局日志表的压缩版 服务类
 * </p>
 *
 * @author bsy
 * @since 2022-06-15
 */
public interface ExpressionLinkResultLogService extends IService<ExpressionLinkResultLog> {

    RestResult<ExpressionLinkResultLogDTO> addOne(AddLinkResultLogRequest addRequest);

    RestResult<ExpressionLinkResultLogDTO> updateOne(EditLinkResultLogRequest editRequest);

    RestResult<List<ExpressionLinkResultLogDTO>> queryDtoList(QueryLinkResultLogRequest queryRequest);

    RestResult<?> logicDeleteByIdList(DeleteByIdListRequest delRequest);
}
