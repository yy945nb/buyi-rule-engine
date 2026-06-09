package com.ymware.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ymware.engine.enums.ExpressionTypeEnum;
import com.ymware.engine.entity.ExpressionExecutorInfoConfig;
import com.ymware.engine.model.request.AddExpressionConfigRequest;
import com.ymware.engine.model.dto.request.DeleteByIdListRequest;
import com.ymware.engine.model.dto.request.EditExpressionConfigRequest;
import com.ymware.engine.model.dto.request.QueryExpressionConfigRequest;
import com.ymware.engine.model.dto.response.ExpressionExecutorDetailConfigDTO;
import com.ymware.engine.model.response.RestResult;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 表达式配置 服务类
 * </p>
 *
 * @author bsy
 * @since 2022-06-12
 */
public interface ExpressionConfigService extends IService<ExpressionExecutorInfoConfig> {

    /**
     * 添加单个表达式
     *
     * @param addRequest
     * @return
     */
    RestResult<ExpressionExecutorDetailConfigDTO> addExpression(AddExpressionConfigRequest addRequest);

    /**
     * 编辑表达式
     *
     * @param editRequest
     * @return
     */
    RestResult<ExpressionExecutorDetailConfigDTO> editExpression(EditExpressionConfigRequest editRequest);

    /**
     * 条件查询表达式
     *
     * @param queryRequest
     * @return
     */
    RestResult<List<ExpressionExecutorDetailConfigDTO>> queryExpression(QueryExpressionConfigRequest queryRequest);

    /**
     * 根据id批量删除表达式
     *
     * @param delRequest
     * @return
     */
    RestResult<?> batchDeleteByIdList(DeleteByIdListRequest delRequest);

    /**
     * 获取事件执行器
     *
     * @param baseId    基础编号
     * @param parentId  上级编号
     * @param eventName 事件名称
     * @param typeEnum  获取分组模板
     * @return
     */
    List<ExpressionExecutorDetailConfigDTO> getEventInfo(Long baseId, Long parentId, String eventName, ExpressionTypeEnum typeEnum);

    /**
     * 获取下级表达式信息da
     *
     * @param baseId
     * @param parentId
     * @return
     */
    List<ExpressionExecutorDetailConfigDTO> getNodeExpressionInfo(Long baseId, Long parentId);

    /**
     * 获取下级相应的原始数据信息
     *
     * @param baseId
     * @return
     */
    public List<ExpressionExecutorInfoConfig> getExpressionListByBaseId(Long baseId);

    /**
     * 拷贝节点
     * @param config
     * @return
     */
    boolean copyNode(ExpressionExecutorInfoConfig config);

    List<ExpressionExecutorInfoConfig> queryExpressionContent(String expressionContent, Date changeDate);
}
