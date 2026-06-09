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
import com.ymware.engine.service.FunctionService;
import com.ymware.engine.vo.function.GetFunctionResponse;
import com.ymware.engine.vo.function.ListFunctionRequest;
import com.ymware.engine.vo.function.ListFunctionResponse;
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
 * @date 2020/8/24
 * @since 1.0.0
 */
@Tag(name = "函数控制器")
@RestController
@RequestMapping("ruleEngine/function")
public class FunctionController {

    @Resource
    private FunctionService functionService;

    /**
     * 函数列表
     *
     * @param pageRequest param
     * @return list
     */
    @PostMapping("list")
    @Operation(summary = "函数列表")
    public PageResult<ListFunctionResponse> list(@RequestBody PageRequest<ListFunctionRequest> pageRequest) {
        return functionService.list(pageRequest);
    }

    /**
     * 查询函数详情
     *
     * @param idRequest 函数id
     * @return 函数信息
     */
    @PostMapping("get")
    @Operation(summary = "查询函数详情")
    public PlainResult<GetFunctionResponse> get(@RequestBody @Valid IdRequest idRequest) {
        PlainResult<GetFunctionResponse> plainResult = new PlainResult<>();
        plainResult.setData(functionService.get(idRequest.getId()));
        return plainResult;
    }


}
