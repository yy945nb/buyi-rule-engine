/*
 * Copyright (c) 2020 dingqianwen (761945125@qq.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ymware.engine.controller;


import com.ymware.engine.common.vo.PlainResult;
import com.ymware.engine.annotation.ReSubmitLock;
import com.ymware.engine.service.ConditionGroupConditionService;
import com.ymware.engine.vo.common.RearrangeRequest;
import com.ymware.engine.vo.condition.SwitchConditionOrderRequest;
import com.ymware.engine.vo.condition.group.condition.DeleteConditionAndBindGroupRefRequest;
import com.ymware.engine.vo.condition.group.condition.SaveConditionAndBindGroupRequest;
import com.ymware.engine.vo.condition.group.condition.SaveConditionAndBindGroupResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/7/14
 * @since 1.0.0
 */
@Tag(name = "条件组条件控制器")
@RestController
@RequestMapping("ruleEngine/conditionGroupCondition")
public class ConditionGroupConditionController {

    @Resource
    private ConditionGroupConditionService conditionGroupConditionService;

    /**
     * 保存条件并绑定到规则条件组中
     *
     * @param saveConditionAndBindGroupRequest 条件信息
     * @return 条件id
     */
    @ReSubmitLock
    @PostMapping("saveConditionAndBindGroup")
    @Operation(summary = "保存条件并绑定到规则条件组中")
    public PlainResult<SaveConditionAndBindGroupResponse> saveConditionAndBindGroup(@RequestBody @Valid SaveConditionAndBindGroupRequest saveConditionAndBindGroupRequest) {
        PlainResult<SaveConditionAndBindGroupResponse> plainResult = new PlainResult<>();
        plainResult.setData(conditionGroupConditionService.saveConditionAndBindGroup(saveConditionAndBindGroupRequest));
        return plainResult;
    }

    /**
     * 删除条件
     *
     * @param deleteConditionAndBindGroupRefRequest 条件id与绑定关系id
     * @return 删除结果
     */
    @ReSubmitLock
    @PostMapping("deleteCondition")
    @Operation(summary = "根据ID删除条件")
    public PlainResult<Boolean> deleteCondition(@RequestBody @Valid DeleteConditionAndBindGroupRefRequest deleteConditionAndBindGroupRefRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(conditionGroupConditionService.deleteCondition(deleteConditionAndBindGroupRefRequest));
        return plainResult;
    }

    /**
     * 交换条件顺序
     *
     * @param switchOrderRequest fromId toId
     * @return true
     */
    @Deprecated
    @PostMapping("switchOrder")
    @Operation(summary = "交换条件顺序")
    public PlainResult<Boolean> switchOrder(@RequestBody @Valid SwitchConditionOrderRequest switchOrderRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(conditionGroupConditionService.switchOrder(switchOrderRequest));
        return plainResult;
    }

    /**
     * 重新排序
     *
     * @param rearrangeRequests r
     * @return true
     */
    @PostMapping("rearrange")
    @Operation(summary = "重新排序")
    public PlainResult<Boolean> rearrange(@RequestBody @Valid List<RearrangeRequest> rearrangeRequests) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(conditionGroupConditionService.rearrange(rearrangeRequests));
        return plainResult;
    }


}
