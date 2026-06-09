package com.ymware.engine.compute.api;

import com.ymware.engine.model.VariableApiModel;

import java.util.List;

/**
 * 变量类型加载
 */
public interface ExpressVariableTypeDocumentLoader {

    /**
     * 加载函数表
     *
     * @return
     */
    List<VariableApiModel> loadList();

}
