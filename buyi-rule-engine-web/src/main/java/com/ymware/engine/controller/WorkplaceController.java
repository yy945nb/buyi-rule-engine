package com.ymware.engine.controller;

import com.ymware.engine.common.vo.PlainResult;
import com.ymware.engine.service.WorkplaceService;
import com.ymware.engine.vo.workplace.HeadInfoResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 〈WorkplaceController〉
 *
 * @author 丁乾文
 * @date 2021/9/9 1:13 下午
 * @since 1.0.0
 */
@Tag(name = "工作台控制器")
@RestController
@RequestMapping("ruleEngine/workplace")
public class WorkplaceController {

    @Resource
    private WorkplaceService workplaceService;


    /**
     * HeadInfo
     *
     * @return r
     */
    @PostMapping("/headInfo")
    @Operation(summary = "HeadInfo")
    public PlainResult<HeadInfoResponse> headInfo() {
        PlainResult<HeadInfoResponse> plainResult = new PlainResult<>();
        plainResult.setData(this.workplaceService.headInfo());
        return plainResult;
    }


}
