package com.ymware.engine.controller;

import com.ymware.engine.common.vo.BaseResult;
import com.ymware.engine.common.vo.IdRequest;
import com.ymware.engine.common.vo.PageRequest;
import com.ymware.engine.common.vo.PlainResult;
import com.ymware.engine.annotation.Auth;
import com.ymware.engine.service.SystemLogService;
import com.ymware.engine.vo.system.log.GetLogResponse;
import com.ymware.engine.vo.system.log.ListLogRequest;
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
 * @date 2021/3/2
 * @since 1.0.0
 */
@Tag(name = "系统日志控制器")
@RestController
@RequestMapping("system/log")
public class SystemLogController {

    @Resource
    private SystemLogService systemLogService;


    /**
     * 查询日志列表
     *
     * @param pageRequest 分页参数
     * @return list
     */
    @PostMapping("list")
    @Operation(summary = "日志列表")
    public BaseResult list(@RequestBody PageRequest<ListLogRequest> pageRequest) {
        return systemLogService.list(pageRequest);
    }

    /**
     * 根据id查询日志详情
     *
     * @param idRequest 日志id
     * @return info
     */
    @PostMapping("get")
    @Operation(summary = "根据id查询日志详情")
    public PlainResult<GetLogResponse> get(@RequestBody @Valid IdRequest idRequest) {
        PlainResult<GetLogResponse> plainResult = new PlainResult<>();
        plainResult.setData(systemLogService.get(idRequest.getId()));
        return plainResult;
    }

    /**
     * 根据id删除日志，只能由管理删除
     *
     * @param idRequest 日志id
     * @return true
     */
    @Auth
    @PostMapping("delete")
    @Operation(summary = "根据id删除日志")
    public PlainResult<Boolean> delete(@RequestBody @Valid IdRequest idRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(systemLogService.delete(idRequest.getId()));
        return plainResult;
    }

}
