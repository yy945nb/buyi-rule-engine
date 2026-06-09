package com.ymware.engine.controller;

import com.ymware.engine.common.vo.IdRequest;
import com.ymware.engine.common.vo.PageRequest;
import com.ymware.engine.common.vo.PageResult;
import com.ymware.engine.common.vo.PlainResult;
import com.ymware.engine.service.OperationRecordService;
import com.ymware.engine.vo.operation.record.OperationRecordRequest;
import com.ymware.engine.vo.operation.record.OperationRecordResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 〈OperationRecordController〉
 *
 * @author 丁乾文
 * @date 2021/9/9 5:25 下午
 * @since 1.0.0
 */
@Tag(name = "工作台控制器")
@RestController
@RequestMapping("ruleEngine/operationRecord")
public class OperationRecordController {

    @Resource
    private OperationRecordService operationRecordService;

    /**
     * 操作记录
     *
     * @param request r
     * @return r
     */
    @PostMapping("/list")
    @Operation(summary = "操作记录")
    public PageResult<OperationRecordResponse> operationRecord(@RequestBody @Valid PageRequest<OperationRecordRequest> request) {
        return this.operationRecordService.operationRecord(request);
    }

    /**
     * 删除操作记录
     *
     * @param idRequest 记录id
     * @return true：删除成功
     */
    @PostMapping("delete")
    @Operation(summary = "根据id删除删除")
    public PlainResult<Boolean> delete(@RequestBody @Valid IdRequest idRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(operationRecordService.delete(idRequest.getId()));
        return plainResult;
    }

}
