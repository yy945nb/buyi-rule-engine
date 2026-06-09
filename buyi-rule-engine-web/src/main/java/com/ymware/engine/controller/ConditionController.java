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


import com.ymware.engine.common.vo.*;
import com.ymware.engine.common.vo.IdRequest;
import com.ymware.engine.common.vo.PageRequest;
import com.ymware.engine.common.vo.PageResult;
import com.ymware.engine.common.vo.Param;
import com.ymware.engine.annotation.ReSubmitLock;
import com.ymware.engine.service.ConditionService;
import com.ymware.engine.vo.condition.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/7/14
 * @since 1.0.0
 */
@Tag(name = "条件控制器")
@RestController
@RequestMapping("ruleEngine/condition")
public class ConditionController {

    @Resource
    private ConditionService conditionService;

    /**
     * 添加条件
     *
     * @param addConditionRequest 条件信息
     * @return 条件id
     */
    @ReSubmitLock
    @PostMapping("add")
    @Operation(summary = "添加条件")
    public com.ymware.engine.common.vo.PlainResult<Long> add(@RequestBody @Valid AddConditionRequest addConditionRequest) {
        PlainResult<Long> plainResult = new PlainResult<>();
        plainResult.setData(conditionService.save(addConditionRequest));
        return plainResult;
    }

    /**
     * 根据id查询条件
     *
     * @param idRequest 条件id
     * @return ConditionResponse
     */
    @PostMapping("get")
    @Operation(summary = "根据id查询条件")
    public PlainResult<ConditionBody> getById(@RequestBody @Valid IdRequest idRequest) {
        PlainResult<ConditionBody> plainResult = new PlainResult<>();
        plainResult.setData(conditionService.getById(idRequest.getId()));
        return plainResult;
    }

    /**
     * 根据id更新条件
     *
     * @param updateConditionRequest 条件信息
     * @return true
     */
    @ReSubmitLock
    @PostMapping("update")
    @Operation(summary = "根据id更新条件")
    public PlainResult<Boolean> update(@RequestBody @Valid UpdateConditionRequest updateConditionRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(conditionService.update(updateConditionRequest));
        return plainResult;
    }

    /**
     * 条件列表
     *
     * @param pageRequest param
     * @return ListConditionResponse
     */
    @PostMapping("list")
    @Operation(summary = "条件列表")
    public PageResult<ListConditionResponse> list(@RequestBody PageRequest<ListConditionRequest> pageRequest) {
        return conditionService.list(pageRequest);
    }

    /**
     * 删除条件
     *
     * @param idRequest 条件id
     * @return true：删除成功
     */
    @PostMapping("delete")
    @Operation(summary = "根据id删除条件")
    public PlainResult<Boolean> delete(@RequestBody @Valid IdRequest idRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(conditionService.delete(idRequest.getId()));
        return plainResult;
    }


    /**
     * 条件名称是否存在
     *
     * @param param 条件名称
     * @return true存在
     */
    @PostMapping("nameIsExists")
    @Operation(summary = "条件名称是否存在")
    public PlainResult<Boolean> nameIsExists(@RequestBody @Valid Param<String> param) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(conditionService.conditionNameIsExists(param.getParam()));
        return plainResult;
    }

}
