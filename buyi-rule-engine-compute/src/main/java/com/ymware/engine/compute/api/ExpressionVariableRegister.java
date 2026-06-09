package com.ymware.engine.compute.api;

import com.ymware.engine.model.request.ContextTemplateRequest;

/**
 * 表达式变量
 */
public interface ExpressionVariableRegister extends ExpressVariableDocumentLoader {

    /**
     * 组名称
     *
     * @return
     */
    default boolean isMatch(String groupName) {
        return true;
    }

    /**
     * 查找对象
     *
     * @param name
     * @return
     */
    public boolean finderVariable(String name);

    /**
     * 变量搜索器
     *
     * @param cache 变量参数
     * @return
     */
    public Object invoke(String name, ContextTemplateRequest cache);

}
