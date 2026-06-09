package com.ymware.engine.compute.api;


import com.ymware.engine.model.FunctionApiModel;
import com.ymware.engine.model.VariableApiModel;

import java.util.List;

public interface DocumentApiExecutor {

    public VariableApiModel getVariableApi(String groupName, String name);

    public FunctionApiModel getFunctionApi(String functionName);

    public List<VariableApiModel> getGroupVariableApi(String group);

    public List<VariableApiModel> getAllVariableApi();

    public List<FunctionApiModel> getAllFunctionApi();

}
