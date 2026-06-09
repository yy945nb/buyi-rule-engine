package com.ymware.engine.controller;

import com.ymware.engine.constants.BaseConstants;
import com.ymware.engine.model.dto.request.LoginModel;
import com.ymware.engine.model.response.RestResult;
import com.ymware.engine.service.IExpressionLoginService;
import com.ymware.engine.service.IExpressionTokenService;
import com.ymware.engine.util.CookiesUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 远端节点提交执行器
 */
@Tag(name = "首页")
@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/")
public class IndexController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IExpressionLoginService loginService;

    @Autowired
    private IExpressionTokenService tokenService;

    @Operation(summary = "登录接口")
    @PostMapping("/login")
    public RestResult<Object> executor(@RequestBody LoginModel loginModel, HttpServletResponse servletResponse) {
        final boolean result = loginService.login(loginModel);
        logger.info(" username :{},password :{} , result:{}", loginModel.getUsername(), loginModel.getPassword(), result);
        if (result) {
            final String token = tokenService.tokenCreated(loginModel);
            Map<String, Object> tokenMap = new HashMap<>();
            int timeOut = 1;
            tokenMap.put("tokenName", BaseConstants.TOKEN_NAME);
            tokenMap.put("tokenValue", token);
            tokenMap.put("tokenTimeoutDay", timeOut);
            CookiesUtil.setCookie(servletResponse, BaseConstants.TOKEN_NAME, token, (int) TimeUnit.SECONDS.toDays(timeOut));
            return RestResult.ok(tokenMap);
        }
        return RestResult.failed("验证失败");
    }

}
