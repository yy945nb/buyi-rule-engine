package com.ymware.engine.controller;

import com.ymware.engine.components.html.DefaultDataRender;
import com.ymware.engine.components.html.HtmlComponentHelper;
import com.ymware.engine.enums.RemoteInvokeTypeEnums;
import com.ymware.engine.model.response.RestResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 远端节点提交执行器
 */
@Tag(name = "远端节点提交执行器")
@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ApiController implements InitializingBean {

    private Map<String, List<DefaultDataRender>> dataMap = new HashMap<>();

    @Operation(summary = "后端枚举常量")
    @GetMapping("/enumList")
    public RestResult<Object> executor(@RequestParam("code") String code) {
        List<DefaultDataRender> defaultDataRenders = dataMap.getOrDefault(code, new ArrayList<>());
        return RestResult.ok(defaultDataRenders);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dataMap.put("remoteInvokeType", HtmlComponentHelper.convertDataRender(RemoteInvokeTypeEnums.class));
    }
}
