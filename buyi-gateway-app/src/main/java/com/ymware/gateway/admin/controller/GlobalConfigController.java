package com.ymware.gateway.admin.controller;

import com.ymware.gateway.admin.model.req.GlobalCustomHeadersUpdateReq;
import com.ymware.gateway.admin.model.rsp.GlobalCustomHeadersRsp;
import com.ymware.gateway.admin.service.GlobalConfigService;
import com.ymware.gateway.common.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 全局配置管理接口
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/global-config")
public class GlobalConfigController {

    private final GlobalConfigService globalConfigService;

    /**
     * 获取全局自定义请求头
     */
    @GetMapping("/custom-headers")
    public Mono<R<GlobalCustomHeadersRsp>> getCustomHeaders() {
        return Mono.fromCallable(() -> globalConfigService.getCustomHeaders())
                .subscribeOn(Schedulers.boundedElastic())
                .map(R::ok);
    }

    /**
     * 更新全局自定义请求头
     */
    @PostMapping("/custom-headers")
    public Mono<R<Void>> updateCustomHeaders(@Valid @RequestBody GlobalCustomHeadersUpdateReq req) {
        return Mono.fromRunnable(() -> globalConfigService.updateCustomHeaders(req))
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(R.ok());
    }
}
