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


import com.ymware.engine.common.vo.IdRequest;
import com.ymware.engine.common.vo.PageRequest;
import com.ymware.engine.common.vo.PageResult;
import com.ymware.engine.common.vo.PlainResult;
import com.ymware.engine.annotation.DataPermission;
import com.ymware.engine.annotation.ReSubmitLock;
import com.ymware.engine.enums.DataType;
import com.ymware.engine.enums.OperationType;
import com.ymware.engine.service.VariableService;
import com.ymware.engine.vo.variable.*;
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
@Tag(name = "变量控制器")
@RestController
@RequestMapping("ruleEngine/variable")
public class VariableController {

    @Resource
    private VariableService variableService;

    /**
     * 添加变量
     *
     * @param addConditionRequest 变量信息
     * @return true
     */
    @ReSubmitLock
    @PostMapping("add")
    @Operation(summary = "添加变量")
    public PlainResult<Boolean> add(@RequestBody @Valid AddVariableRequest addConditionRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(variableService.add(addConditionRequest));
        return plainResult;
    }

    /**
     * 根据id查询变量
     *
     * @param idRequest 变量id
     * @return var
     */
    @DataPermission(id = "#idRequest.id", dataType = DataType.VARIABLE, operationType = OperationType.SELECT)
    @PostMapping("get")
    @Operation(summary = "根据id查询变量")
    public PlainResult<GetVariableResponse> get(@RequestBody @Valid IdRequest idRequest) {
        PlainResult<GetVariableResponse> plainResult = new PlainResult<>();
        plainResult.setData(variableService.get(idRequest.getId()));
        return plainResult;
    }

    /**
     * 根据id更新变量
     *
     * @param updateVariableRequest param
     * @return true
     */
    @DataPermission(id = "#updateVariableRequest.id", dataType = DataType.VARIABLE, operationType = OperationType.UPDATE)
    @ReSubmitLock
    @PostMapping("update")
    @Operation(summary = "根据id更新变量")
    public PlainResult<Boolean> update(@RequestBody @Valid UpdateVariableRequest updateVariableRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(variableService.update(updateVariableRequest));
        return plainResult;
    }

    /**
     * 变量列表
     *
     * @param pageRequest param
     * @return result
     */
    @PostMapping("list")
    @Operation(summary = "变量列表")
    public PageResult<ListVariableResponse> list(@RequestBody PageRequest<ListVariableRequest> pageRequest) {
        return this.variableService.list(pageRequest);
    }

    /**
     * 根据id删除变量
     *
     * @param idRequest 变量id
     * @return true
     */
    @DataPermission(id = "#idRequest.id", dataType = DataType.VARIABLE, operationType = OperationType.DELETE)
    @PostMapping("delete")
    @Operation(summary = "根据id删除变量")
    public PlainResult<Boolean> delete(@RequestBody @Valid IdRequest idRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(variableService.delete(idRequest.getId()));
        return plainResult;
    }

    /**
     * 变量名称是否存在
     *
     * @param verifyVariableNameRequest 变量名称
     * @return true存在
     */
    @PostMapping("nameIsExists")
    @Operation(summary = "变量名称是否存在")
    public PlainResult<Boolean> nameIsExists(@RequestBody @Valid VerifyVariableNameRequest verifyVariableNameRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(variableService.varNameIsExists(verifyVariableNameRequest));
        return plainResult;
    }

}
