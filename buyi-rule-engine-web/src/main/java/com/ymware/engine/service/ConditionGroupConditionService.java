package com.ymware.engine.service;

import com.ymware.engine.vo.common.RearrangeRequest;
import com.ymware.engine.vo.condition.SwitchConditionOrderRequest;
import com.ymware.engine.vo.condition.group.condition.DeleteConditionAndBindGroupRefRequest;
import com.ymware.engine.vo.condition.group.condition.SaveConditionAndBindGroupRequest;
import com.ymware.engine.vo.condition.group.condition.SaveConditionAndBindGroupResponse;

import java.util.List;

/**
 * 〈ConditionGroupConditionService〉
 *
 * @author 丁乾文
 * @date 2021/7/12 1:46 下午
 * @since 1.0.0
 */
public interface ConditionGroupConditionService {

    /**
     * 保存条件并绑定到规则条件组中
     *
     * @param saveConditionAndBindGroupRequest 条件信息
     * @return 条件id
     */
    SaveConditionAndBindGroupResponse saveConditionAndBindGroup(SaveConditionAndBindGroupRequest saveConditionAndBindGroupRequest);

    /**
     * 根据id删除条件
     *
     * @param deleteConditionAndBindGroupRefRequest 条件id与绑定关系id
     * @return 删除结果
     */
    Boolean deleteCondition(DeleteConditionAndBindGroupRefRequest deleteConditionAndBindGroupRefRequest);

    /**
     * 交换条件顺序
     *
     * @param switchOrderRequest fromId toId
     * @return boolean
     */
    Boolean switchOrder(SwitchConditionOrderRequest switchOrderRequest);

    /**
     * 重新排序
     *
     * @param rearrangeRequests r
     * @return true
     */
    Boolean rearrange(List<RearrangeRequest> rearrangeRequests);
}
