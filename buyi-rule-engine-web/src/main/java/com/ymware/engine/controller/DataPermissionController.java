package com.ymware.engine.controller;

import com.ymware.engine.common.vo.BaseResult;
import com.ymware.engine.common.vo.PageRequest;
import com.ymware.engine.common.vo.PageResult;
import com.ymware.engine.common.vo.PlainResult;
import com.ymware.engine.service.DataPermissionService;
import com.ymware.engine.vo.permission.data.ListDataPermissionRequest;
import com.ymware.engine.vo.permission.data.ListDataPermissionResponse;
import com.ymware.engine.vo.permission.data.UpdateDataPermissionRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/***
 * 数据权限控制器
 *
 * @author niuxiangqian
 * @version 1.0
 * @since 2021/6/25 7:43 下午
 **/
@Tag(name = "数据权限控制器")
@RestController
@RequestMapping("ruleEngine/dataPermission")
public class DataPermissionController {

    @Resource
    private DataPermissionService dataPermissionService;

    /**
     * 数据权限列表
     *
     * @param pageRequest p
     * @return r
     */
    @PostMapping("list")
    @Operation(summary = "数据权限列表")
    public PageResult<ListDataPermissionResponse> list(@RequestBody @Valid PageRequest<ListDataPermissionRequest> pageRequest) {
        return this.dataPermissionService.list(pageRequest);
    }

    /**
     * 保存或者更新数据权限
     *
     * @param updateRequest u
     * @return r
     */
    @PostMapping("saveOrUpdateDataPermission")
    @Operation(summary = "更新数据权限")
    public BaseResult update(@RequestBody @Valid UpdateDataPermissionRequest updateRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(dataPermissionService.saveOrUpdateDataPermission(updateRequest));
        return plainResult;
    }

}
