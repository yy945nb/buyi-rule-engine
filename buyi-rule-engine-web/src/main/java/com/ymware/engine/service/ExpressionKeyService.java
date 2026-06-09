package com.ymware.engine.service;

import com.ymware.engine.model.GlobalExpressionDocInfo;
import com.ymware.engine.model.request.ContextTemplateRequest;
import com.ymware.engine.model.dto.function.FunctionInfoDto;
import com.ymware.engine.model.variable.VariableInfoDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 表达式关键字管理
 */
public interface ExpressionKeyService {
    /**
     * 变量是否定义
     * @param key
     * @return
     */
    public boolean isVariableDefined(String key);

    /**
     * 函数是否定义
     * @param key
     * @return
     */
    public boolean isFunctionDefined(String key);

    /**
     * 获取变量定义
     * @param key
     * @return
     */
    public VariableInfoDto getVariableDefined(String key);

    /**
     * 加载所有变量定义
     * @return
     */
    public List<VariableInfoDto> loadAllVariableDefined();

    /**
     * 加载所有函数定义
     * @return
     */
    public List<FunctionInfoDto> loadAllFunctionDefined();

    /**
     * 获取函数的值
     * @param key
     * @param request
     * @param param
     * @return
     * @throws Exception
     */
    public Object invokeFunction(String key, ContextTemplateRequest request, Map<String, Object> param) throws Exception;

    /**
     * 变量执行
     * @param key
     * @param request
     * @return
     * @throws Exception
     */
    public Object invokeVariable(String key, ContextTemplateRequest request) throws Exception;

    /**
     * 多个变量执行
     * @param keys
     * @param request
     * @return
     * @throws Exception
     */
    public Map<String, Object> invokeVariable(Collection<String> keys, ContextTemplateRequest request) throws Exception;

    /**
     * 刷新远端变量
     * @param serviceName
     * @return
     */
    public boolean refreshDocument(GlobalExpressionDocInfo serviceName);


}
