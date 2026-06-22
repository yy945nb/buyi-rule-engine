package com.ymware.engine.domain.workflow.model;

/**
 * 链执行生命周期钩子回调接口
 * 替代 Chain 上的 protected onNode* 方法，支持外部注入自定义行为
 */
public interface ChainExecutionHooks {

    /** 节点执行前调用（参数解析之前） */
    void onNodeExecuteBefore(NodeContext nodeContext);

    /** 节点开始执行时调用（参数解析之后，execute() 之前） */
    void onNodeExecuteStart(NodeContext nodeContext);

    /** 节点执行结束时调用（finally 块中） */
    void onNodeExecuteEnd(NodeContext nodeContext);

    /** 节点完整生命周期结束后调用（processSubsequentNodes 之后） */
    void onNodeExecuteAfter(NodeContext nodeContext);

    /** 空实现，不需要钩子时使用 */
    ChainExecutionHooks NOOP = new ChainExecutionHooks() {
        @Override public void onNodeExecuteBefore(NodeContext ctx) {}
        @Override public void onNodeExecuteStart(NodeContext ctx) {}
        @Override public void onNodeExecuteEnd(NodeContext ctx) {}
        @Override public void onNodeExecuteAfter(NodeContext ctx) {}
    };
}
