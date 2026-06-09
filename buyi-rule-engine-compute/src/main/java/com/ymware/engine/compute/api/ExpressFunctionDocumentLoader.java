package com.ymware.engine.compute.api;

import com.ymware.engine.model.FunctionApiModel;

import java.util.Collections;
import java.util.List;

/**
 * 函数文档加载器
 */
public interface ExpressFunctionDocumentLoader {

    /**
     * 是否需要动态刷新
     *
     * @return
     */
    default boolean isDynamicRefresh() {
        return false;
    }

    /**
     * 加载函数表
     *
     * @return
     */
    default List<FunctionApiModel> loadFunctionList() {
        FunctionApiModel functionApiModel = loadFunctionInfo();
        if (functionApiModel != null) {
            return Collections.singletonList(functionApiModel);
        }
        return Collections.emptyList();
    }

    default FunctionApiModel loadFunctionInfo() {
        return null;
    }
}
