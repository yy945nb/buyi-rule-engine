package com.ymware.engine.service;

import com.ymware.engine.model.dto.request.LoginModel;


public interface IExpressionTokenService {

    public String tokenCreated(LoginModel loginModel);

    public boolean checkToken(String token);

}
