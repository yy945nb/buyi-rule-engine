package com.ymware.engine.domain.workflow.model;

import com.ymware.engine.domain.workflow.event.*;
import com.ymware.engine.domain.workflow.listener.*;
import com.ymware.engine.domain.value.model.Parameter;
import com.ymware.engine.domain.workflow.type.*;
import com.ymware.engine.workflow.tools.NamedThreadPools;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.*;

/**
 * 链类 - 继承自ChainNode，直接集成执行逻辑
 * 参考Agents-Flex Core的简洁架构设计
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/08/22
 */
@Getter
public class Chain extends ChainNode {

    // 链特有的属性
    private List<ChainNode> nodes;
    private List<ChainEdge> edges;
    private Chain parent;
    private List<Chain> children;

    // 执行相关属性
    private Map<String, Object> executeResult = null;
    private Map<String, Object> outputResult;
    private final Map<String, Object> memory = new ConcurrentHashMap<>();
    private ChainStatus chainStatus = ChainStatus.READY;
    private Exception exception;
    private String message;

    // 事件总线 - 统一管理所有监听器和事件通知
    @Getter
    private final ChainEventBus eventBus = new ChainEventBus();

    // 异步执行相关
    private ExecutorService asyncNodeExecutors = NamedThreadPools.newFixedThreadPool("chain-executor");
    private Phaser phaser = new Phaser(1);
    private final Map<String, NodeContext> nodeContexts = new ConcurrentHashMap<>();
    private final Map<String, ChainNode> suspendNodes = new ConcurrentHashMap<>();
    private List<Parameter> suspendForParameters;

    // 节点执行信息映射
    private final Map<String, ChainNodeExecuteInfo> executeInfoMap = new ConcurrentHashMap<>();

    // 异步执行相关字段
    private ScheduledExecutorService progressUpdateExecutor = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean asyncExecutionCompleted = false;
    private CompletableFuture<Map<String, Object>> asyncExecutionFuture;

    // 节点索引 - O(1) 查找
    private final Map<String, ChainNode> nodeIndex = new ConcurrentHashMap<>();

    // 执行器 - 延迟初始化，支持子类钩子
    private ChainExecutor executor;

    public Chain() {
        this.id = UUID.randomUUID().toString();
        this.nodeType = NodeType.CHAIN;
    }

    private ChainExecutor getExecutor() {
        if (executor == null) {
            executor = new ChainExecutor(this, new ChainExecutionHooks() {
                @Override public void onNodeExecuteBefore(NodeContext ctx) { Chain.this.onNodeExecuteBefore(ctx); }
                @Override public void onNodeExecuteStart(NodeContext ctx) { Chain.this.onNodeExecuteStart(ctx); }
                @Override public void onNodeExecuteEnd(NodeContext ctx) { Chain.this.onNodeExecuteEnd(ctx); }
                @Override public void onNodeExecuteAfter(NodeContext ctx) { Chain.this.onNodeExecuteAfter(ctx); }
            });
        }
        return executor;
    }

    // ==================== 节点管理 ====================

    public List<ChainNode> addNode(ChainNode node) {
        if (nodes == null) {
            nodes = new ArrayList<>();
        }

        if (node instanceof ChainEventListener) {
            eventBus.addEventListener((ChainEventListener) node);
        }

        if (node.getId() == null) {
            node.setId(UUID.randomUUID().toString());
        }

        if (node instanceof Chain) {
            ((Chain) node).parent = this;
            ((Chain) node).eventBus.setParent(this);
            addChild((Chain) node);
        }

        nodes.add(node);
        nodeIndex.put(node.getId(), node);
        return nodes;
    }

    private void addChild(Chain child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }

    public List<ChainEdge> addEdge(ChainEdge edge) {
        if (edges == null) {
            edges = new ArrayList<>();
        }
        edges.add(edge);

        boolean findSource = false, findTarget = false;

        for (ChainNode node : nodes) {
            if (node.getId().equals(edge.getSource())) {
                node.addOutwardEdge(edge);
                findSource = true;
            } else if (node.getId().equals(edge.getTarget())) {
                node.addInwardEdge(edge);
                findTarget = true;
            }
            if (findSource && findTarget) {
                break;
            }
        }
        return edges;
    }

    // ==================== 事件监听器管理 (委托给 eventBus) ====================

    public void addEventListener(Class<? extends ChainEvent> eventClass, ChainEventListener listener) {
        eventBus.addEventListener(eventClass, listener);
    }

    public void addEventListener(ChainEventListener listener) {
        eventBus.addEventListener(listener);
    }

    public void removeEventListener(ChainEventListener listener) {
        eventBus.removeEventListener(listener);
    }

    public void removeEventListener(Class<? extends ChainEvent> eventClass, ChainEventListener listener) {
        eventBus.removeEventListener(eventClass, listener);
    }

    public void addErrorListener(ChainErrorListener listener) {
        eventBus.addErrorListener(listener);
    }

    public void removeErrorListener(ChainErrorListener listener) {
        eventBus.removeErrorListener(listener);
    }

    public void addNodeErrorListener(NodeErrorListener listener) {
        eventBus.addNodeErrorListener(listener);
    }

    public void removeNodeErrorListener(NodeErrorListener listener) {
        eventBus.removeNodeErrorListener(listener);
    }

    public void addSuspendListener(ChainSuspendListener listener) {
        eventBus.addSuspendListener(listener);
    }

    public void removeSuspendListener(ChainSuspendListener listener) {
        eventBus.removeSuspendListener(listener);
    }

    public void addOutputListener(ChainOutputListener outputListener) {
        eventBus.addOutputListener(outputListener);
    }

    public void addListener(ChainExecutionListener listener) {
        eventBus.addExecutionListener(listener);
    }

    public void removeListener(ChainExecutionListener listener) {
        eventBus.removeExecutionListener(listener);
    }

    // ==================== 执行逻辑（委托给 ChainExecutor） ====================

    @Override
    public Map<String, Object> execute(Chain parent) {
        return executeForResult(parent.getMemory());
    }

    protected void executeInternal() {
        getExecutor().executeInternal();
    }

    public void execute(Map<String, Object> variables) {
        getExecutor().execute(variables);
    }

    public Map<String, Object> executeForResult(Map<String, Object> variables) {
        return executeForResult(variables, false);
    }

    public Map<String, Object> executeForResult(Map<String, Object> variables, boolean ignoreError) {
        return getExecutor().executeForResult(variables, ignoreError);
    }

    public CompletableFuture<Map<String, Object>> executeAsync(Map<String, Object> variables) {
        return getExecutor().executeAsync(variables);
    }

    @Override
    public List<Parameter> getParameters() {
        if (this.nodes == null || this.nodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<ChainNode> startNodes = new ArrayList<>();
        for (ChainNode node : this.nodes) {
            if (node.getInwardEdges() == null || node.getInwardEdges().isEmpty()) {
                startNodes.add(node);
            }
        }

        if (startNodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<Parameter> parameters = new ArrayList<>();
        for (ChainNode node : startNodes) {
            List<Parameter> nodeParameters = node.getParameters();
            if (nodeParameters != null) parameters.addAll(nodeParameters);
        }
        return parameters;
    }

    // ==================== 辅助方法 ====================

    public NodeContext getNodeContext(ChainNode chainNode) {
        return nodeContexts.computeIfAbsent(chainNode.getId(), k -> new NodeContext());
    }

    /**
     * 获取节点执行结果
     */
    public Map<String, Object> getNodeExecuteResult(String nodeId) {
        Map<String, Object> all = memory;
        Map<String, Object> result = new HashMap<>();
        all.forEach((k, v) -> {
            if (k.startsWith(nodeId + ".")) {
                String newKey = k.substring(nodeId.length() + 1);
                result.put(newKey, v);
            }
        });
        return result;
    }

    public void setStatusAndNotifyEvent(ChainStatus status) {
        ChainStatus before = this.chainStatus;
        this.chainStatus = status;

        if (before != status) {
            eventBus.notifyEvent(new ChainStatusChangeEvent(this, this.chainStatus, before));
        }
    }

    // ==================== 钩子方法 ====================

    protected void onNodeExecuteAfter(NodeContext nodeContext) {
    }

    protected void onNodeExecuteEnd(NodeContext nodeContext) {
    }

    protected void onNodeExecuteStart(NodeContext nodeContext) {
    }

    protected void onNodeExecuteBefore(NodeContext nodeContext) {
    }

    // ==================== 其他方法 ====================

    public void stopNormal(String message) {
        this.message = message;
        setStatusAndNotifyEvent(ChainStatus.FINISHED_NORMAL);
    }

    public void stopError(String message) {
        this.message = message;
        setStatusAndNotifyEvent(ChainStatus.FINISHED_ABNORMAL);
    }

    public void output(ChainNode node, Object response) {
        eventBus.notifyOutput(this, node, response);
    }

    public void suspend(ChainNode node) {
        try {
            suspendNodes.putIfAbsent(node.getId(), node);
        } finally {
            setStatusAndNotifyEvent(ChainStatus.SUSPEND);
        }
    }

    public void resume(Map<String, Object> variables) {
        getExecutor().execute(variables);
    }

    public void reset() {
        // node
        this.memory.clear();
        this.setStatus(ChainNodeStatus.READY);

        // chain
        this.chainStatus = ChainStatus.READY;
        this.executeResult = null;
        this.outputResult = null;
        this.message = null;
        this.exception = null;
        this.nodeContexts.clear();

        if (this.suspendNodes != null) {
            this.suspendNodes.clear();
        }

        if (this.suspendForParameters != null) {
            this.suspendForParameters.clear();
        }

        this.phaser = new Phaser(1);
        this.executor = null;
    }

    // ==================== 内部类 ====================

    public static class ExecuteNode {
        final ChainNode currentNode;
        final ChainNode prevNode;
        final String fromEdgeId;

        public ExecuteNode(ChainNode currentNode, ChainNode prevNode, String fromEdgeId) {
            this.currentNode = currentNode;
            this.prevNode = prevNode;
            this.fromEdgeId = fromEdgeId;
        }
    }

    // ==================== Getter/Setter ====================

    public void setNodes(List<ChainNode> nodes) {
        this.nodes = nodes;
    }

    public void setEdges(List<ChainEdge> edges) {
        this.edges = edges;
    }

    public void setParent(Chain parent) {
        this.parent = parent;
    }

    public void setChildren(List<Chain> children) {
        this.children = children;
    }

    public void setExecuteResult(Map<String, Object> executeResult) {
        this.executeResult = executeResult;
    }

    public void setOutputResult(Map<String, Object> outputResult) {
        this.outputResult = outputResult;
    }

    public void setMemory(Map<String, Object> memory) {
        this.memory.clear();
        if (memory != null) {
            this.memory.putAll(memory);
        }
    }

    public void setStatus(ChainStatus status) {
        this.chainStatus = status;
    }

    public ChainStatus getChainStatus() {
        return chainStatus;
    }

    @Override
    public ChainNodeStatus getStatus() {
        // 将ChainStatus转换为ChainNodeStatus
        switch (chainStatus) {
            case READY:
                return ChainNodeStatus.READY;
            case RUNNING:
                return ChainNodeStatus.RUNNING;
            case FINISHED_NORMAL:
                return ChainNodeStatus.FINISHED;
            case FINISHED_ABNORMAL:
                return ChainNodeStatus.FINISHED_ABNORMAL;
            case ERROR:
                return ChainNodeStatus.FAILED;
            case SUSPEND:
                return ChainNodeStatus.READY; // 暂时映射为READY
            default:
                return ChainNodeStatus.READY;
        }
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAsyncNodeExecutors(ExecutorService asyncNodeExecutors) {
        this.asyncNodeExecutors = asyncNodeExecutors;
    }

    public void setPhaser(Phaser phaser) {
        this.phaser = phaser;
    }

    public void setNodeContexts(Map<String, NodeContext> nodeContexts) {
        this.nodeContexts.clear();
        if (nodeContexts != null) {
            this.nodeContexts.putAll(nodeContexts);
        }
    }

    public void setSuspendNodes(Map<String, ChainNode> suspendNodes) {
        this.suspendNodes.clear();
        if (suspendNodes != null) {
            this.suspendNodes.putAll(suspendNodes);
        }
    }

    public void setSuspendForParameters(List<Parameter> suspendForParameters) {
        this.suspendForParameters = suspendForParameters;
    }

    public void setChainStatus(ChainStatus chainStatus) {
        this.chainStatus = chainStatus;
    }

    public void setAsyncExecutionCompleted(boolean asyncExecutionCompleted) {
        this.asyncExecutionCompleted = asyncExecutionCompleted;
    }

    public void setAsyncExecutionFuture(CompletableFuture<Map<String, Object>> asyncExecutionFuture) {
        this.asyncExecutionFuture = asyncExecutionFuture;
    }

    public void addSuspendForParameter(Parameter suspendForParameter) {
        if (this.suspendForParameters == null) {
            this.suspendForParameters = new ArrayList<>();
        }
        this.suspendForParameters.add(suspendForParameter);
    }

    /**
     * 关闭异步执行相关资源
     */
    public void shutdownAsyncExecution() {
        if (progressUpdateExecutor != null && !progressUpdateExecutor.isShutdown()) {
            progressUpdateExecutor.shutdown();
            try {
                if (!progressUpdateExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    progressUpdateExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                progressUpdateExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        if (asyncNodeExecutors != null && !asyncNodeExecutors.isShutdown()) {
            asyncNodeExecutors.shutdown();
            try {
                if (!asyncNodeExecutors.awaitTermination(5, TimeUnit.SECONDS)) {
                    asyncNodeExecutors.shutdownNow();
                }
            } catch (InterruptedException e) {
                asyncNodeExecutors.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 获取节点参数数据（兼容旧API）
     */
    public Map<String, Object> getParametersData(ChainNode node) {
        return ParameterResolver.resolveFromMemory(node.getParameters(), getMemory());
    }

    public void clearExecuteInfoMap(){
        this.executeInfoMap.clear();
    }
}
