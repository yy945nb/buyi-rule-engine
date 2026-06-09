package com.ymware.engine.service.impl;

import com.ymware.engine.config.props.ExpressionServerProperties;
import com.ymware.engine.model.dto.request.LoginModel;
import com.ymware.engine.service.IExpressionTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

/**
 * 默认的MD5token授权校验
 */
@Service
public class ExpressionMd5TokenServiceImpl implements IExpressionTokenService {

    @Autowired
    private ExpressionServerProperties serverProperties;

    @Override
    public String tokenCreated(LoginModel loginModel) {
        final String username = loginModel.getUsername();
        return md5(username);
    }

    @Override
    public boolean checkToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        final String username = serverProperties.getUsername();
        final String userToken = md5(username);
        return userToken.equals(token);
    }

    private String md5(String input) {
        return DigestUtils.md5DigestAsHex(input.getBytes());
    }
}
