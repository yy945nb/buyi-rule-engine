package com.ymware.engine.service.impl;

import com.ymware.engine.config.props.ExpressionServerProperties;
import com.ymware.engine.model.dto.request.LoginModel;
import com.ymware.engine.service.IExpressionLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class ExpressionLoginServiceImpl implements IExpressionLoginService {

    @Autowired
    private ExpressionServerProperties properties;

    @Override
    public boolean login(LoginModel loginModel) {
        final String username = properties.getUsername();
        final String password = properties.getPassword();

        if (!(StringUtils.hasText(username) && StringUtils.hasText(password))) {
            return false;
        }

        return username.equals(loginModel.getUsername()) && password.equals(loginModel.getPassword());
    }
}
