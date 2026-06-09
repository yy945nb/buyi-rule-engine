package com.ymware.engine.service;

import com.ymware.engine.model.VariableApiModel;

import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author liukaixiong
 * @date 2024/11/12 - 16:51
 */
public interface ExpressionVarTypeService {
    public List<VariableApiModel> getKeyInfo(String serviceName, String type);

    public boolean refresh(String serviceName, List<VariableApiModel> variableInfoDto);

    public Set<Object> getAllTypeList(String serviceName);

}
