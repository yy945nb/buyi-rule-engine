//package com.ymware.engine.expression;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.json.JSONUtil;
//import components.com.ymware.engine.TraceLogHelper;
//import enums.com.ymware.engine.TraceStageEnums;
//import exception.com.ymware.engine.Throws;
//import service.com.ymware.engine.ExpressionKeyService;
//import com.ymware.engine.compute.process.ExpressionVariableProcessAdaptor;
//import com.ymware.engine.annotation.VariableKey;
//import model.com.ymware.engine.core.ContextTemplateRequest;
//import utils.com.ymware.engine.core.Jsons;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Collection;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * 本地注册通用的变量
// *
// * @author liukaixiong
// * @date : 2022/6/9 - 14:23
// */
//@Component
//@Slf4j
//@Deprecated
//public class LocalVariableRegister extends ExpressionVariableProcessAdaptor<Object> {
//
//    @Autowired
//    private ExpressionKeyService expressionKeyService;
//
//    @Override
//    public Map<String, Object> processList(Collection<String> variableNameList, ContextTemplateRequest contextTemplateCache) {
//        Map<String, Object> resultMap = new LinkedHashMap<>();
//
//        // 先获取本地的变量解释
//        Set<String> localVariableList = variableNameList.stream().filter(this::isLocalVariable).collect(Collectors.toSet());
//
//        localVariableList.forEach(key -> resultMap.put(key, process(key, contextTemplateCache)));
//
//        Set<String> remoteVariableList = variableNameList.stream().filter(name -> !this.isLocalVariable(name)).collect(Collectors.toSet());
//
//        Set<String> notMatch = remoteVariableList.stream().filter(name -> !expressionKeyService.isVariableDefined(name)).collect(Collectors.toSet());
//
//        Throws.check(CollectionUtil.isNotEmpty(notMatch), String.format("变量没有找到定义:%s", Jsons.toJsonString(notMatch)));
//
//        try {
//            resultMap.putAll(expressionKeyService.invokeVariable(remoteVariableList, contextTemplateCache));
//        } catch (Exception e) {
//            TraceLogHelper.recordError(contextTemplateCache, e);
//            log.error("变量执行异常", e);
//        }
//
//        // 重新定义一套基于批量变量的执行。
//        return resultMap;
//    }
//
//
//    @Override
//    protected Object defaultExceptionInvoke(Exception e, String name, ContextTemplateRequest cache) {
//        TraceLogHelper.recordError(cache, e);
//        return super.defaultExceptionInvoke(e, name, cache);
//    }
//
//    @Override
//    protected Object defaultNoNameInvoke(String name, ContextTemplateRequest cache) {
//        TraceLogHelper.recordLog(cache, TraceStageEnums.VARIABLE_PARSE, String.format("变量没有找到定义:%s", name));
//        return super.defaultNoNameInvoke(name, cache);
//    }
//
//    @VariableKey(name = "local_test", describe = "本地测试变量")
//    private boolean localTest(ContextTemplateRequest cache) {
//        log.info("本地测试变量被触发~：{}", JSONUtil.toJsonStr(cache));
//        return true;
//    }
//
//}
