package com.ymware.engine.executor.impl;

import cn.hutool.json.JSONUtil;
import com.ymware.engine.result.ApiResult;
import com.ymware.engine.model.RemoteExpressionModel;
import com.ymware.engine.enums.RemoteInvokeTypeEnums;
import com.ymware.engine.executor.ExpressionRemoteInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * HTTP远程调用实现
 */
@Component
@Slf4j
public class HttpRemoteInvoke implements ExpressionRemoteInvoker {

    @Autowired(required = false)
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public RemoteInvokeTypeEnums type() {
        return RemoteInvokeTypeEnums.HTTP;
    }

    @Override
    public ApiResult<String> invoke(String url, RemoteExpressionModel expressionModel) throws Exception {
        String body = JSONUtil.toJsonStr(expressionModel);
        log.debug(" URL: {}", url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<ApiResult> response = getRestTemplate().postForEntity(url, request, ApiResult.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.warn("http请求失败：{} -> {}", url, response.getBody().getMessage());
            return ApiResult
                    .error(response.getStatusCode().value(), Objects.requireNonNull(response.getBody()).getMessage());
        }

        ApiResult apiResult = response.getBody();
        log.debug("http 返回结果：{}", apiResult);
        Object data = apiResult.getData();
        return ApiResult.ok(data.toString());
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
