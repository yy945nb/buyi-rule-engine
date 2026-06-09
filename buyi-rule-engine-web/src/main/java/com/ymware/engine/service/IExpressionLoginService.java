package com.ymware.engine.service;

import com.ymware.engine.model.dto.request.LoginModel;

/**
 * 登录逻辑
 *
 * @author liukaixiong
 * @date 2024/10/15 - 13:25
 */
public interface IExpressionLoginService {


    public boolean login(LoginModel loginModel);


}
