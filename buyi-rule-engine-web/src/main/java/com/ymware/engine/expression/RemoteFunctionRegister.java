//package com.ymware.engine.expression;
//
//import cn.hutool.core.convert.Convert;
//import service.com.ymware.engine.ExpressionKeyService;
//import com.ymware.engine.compute.api.ExpressFunctionDocumentLoader;
//import com.ymware.engine.compute.api.ExpressionFunctionRegister;
//import api.model.api.com.ymware.engine.core.FunctionApiModel;
//import model.com.ymware.engine.core.ContextTemplateRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * 远端函数获取
// *
// * @author liukaixiong
// * @date : 2022/6/9 - 14:06
// */
//@Component
//@Deprecated
//public class RemoteFunctionRegister implements ExpressionFunctionRegister<Object>, ExpressFunctionDocumentLoader {
//
//    @Autowired
//    private ExpressionKeyService expressionKeyService;
//
//    @Override
//    public boolean finderFunction(String functionName) {
//        return expressionKeyService.isFunctionDefined(functionName);
//    }
//
//    @Override
//    public Object call(String functionName, ContextTemplateRequest env, Map<String, Object> request) throws Exception {
//        return expressionKeyService.invokeFunction(functionName, env, request);
//    }
//
//    @Override
//    public List<FunctionApiModel> loadFunctionList() {
//        return expressionKeyService.loadAllFunctionDefined().stream()
//            .map(fun -> Convert.convert(FunctionApiModel.class, fun)).collect(Collectors.toList());
//    }
//
//    @Override
//    public boolean isDynamicRefresh() {
//        // 表示需要动态刷新
//        return true;
//    }
//}
