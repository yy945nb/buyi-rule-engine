package com.ymware.engine.domain.workflow.model;

import com.ymware.engine.domain.workflow.event.*;
import com.ymware.engine.domain.workflow.exception.ChainException;
import com.ymware.engine.domain.workflow.type.*;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 工作流执行引擎 - 从 Chain 中提取的执行逻辑
 * 负责节点遍历、异步调度、生命周期管理和事件通知
 *
 * Chain 将自身引用传递给 ChainExecutor，Executor 直接读写 Chain 的执行状态字段
 */
@Slf4j
public class ChainExecutor {

    private final Chain chain;
    private final ChainExecutionHooks hooks;

    public ChainExecutor(Chain chain, ChainExecutionHooks hooks) {
        this.chain = Objects.requireNonNull(chain, "chain must not be null");
        this.hooks = hooks != null ? hooks : ChainExecutionHooks.NOOP;
    }

    public ChainExecutor(Chain chain) {
        this(chain, ChainExecutionHooks.NOOP);
    }

    // ==================== 公开入口（由 Chain 门面调用） ====================

    public void execute(Map<String, Object> variables) {
        runInLifeCycle(variables,
                new ChainStartEvent(chain, variables),
                this::executeInternal);
    }

    public Map<String, Object> executeForResult(Map<String, Object> variables, boolean ignoreError) {
        if (chain.getChainStatus() == ChainStatus.SUSPEND) {
            chain.resume(variables);
        } else {
            runInLifeCycle(variables, new ChainStartEvent(chain, variables), this::executeInternal);
        }

        if (!ignoreError) {
            if (chain.getChainStatus() == ChainStatus.FINISHED_ABNORMAL) {
                Exception ex = chain.getException();
                if (ex != null) {
                    if (ex instanceof RuntimeException) {
                        throw (RuntimeException) ex;
                    } else {
                        throw new ChainException(ex);
                    }
                } else {
                    String msg = chain.getMessage();
                    if (msg == null) msg = "Chain execute error";
                    throw new ChainException(msg);
                }
            }
        }

        return chain.getOutputResult();
    }

    public CompletableFuture<Map<String, Object>> executeAsync(Map<String, Object> variables) {
        if (chain.getAsyncExecutionFuture() != null && !chain.getAsyncExecutionFuture().isDone()) {
            return chain.getAsyncExecutionFuture();
        }

        chain.setAsyncExecutionCompleted(false);
        chain.setChainStatus(ChainStatus.RUNNING);

        if (variables != null) {
            chain.getMemory().putAll(variables);
        }

        CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> {
            try {
                notifyExecutionStart();

                ScheduledFuture<?> progressUpdateTask = startProgressUpdateTask();

                List<ChainNode> startNodes = getStartNodes();
                if (startNodes != null && !startNodes.isEmpty()) {
                    doExecuteChainNodesAsync(startNodes);
                }

                waitForCompletion();

                progressUpdateTask.cancel(false);

                if (chain.getChainStatus() == ChainStatus.FINISHED_ABNORMAL) {
                    Exception executionException = chain.getException();
                    if (executionException != null) {
                        notifyExecutionComplete(null, executionException);
                        if (executionException instanceof RuntimeException) {
                            throw (RuntimeException) executionException;
                        } else {
                            throw new ChainException(executionException);
                        }
                    } else {
                        String errorMsg = chain.getMessage() != null ? chain.getMessage() : "Chain execute error";
                        notifyExecutionComplete(null, new ChainException(errorMsg));
                        throw new ChainException(errorMsg);
                    }
                }

                chain.setAsyncExecutionCompleted(true);
                chain.setChainStatus(ChainStatus.FINISHED_NORMAL);
                notifyExecutionComplete(chain.getOutputResult(), null);

                return chain.getOutputResult();

            } catch (Exception e) {
                chain.setChainStatus(ChainStatus.FINISHED_ABNORMAL);
                chain.setException(e);
                notifyExecutionComplete(null, e);
                throw e;
            }
        }, chain.getAsyncNodeExecutors());

        chain.setAsyncExecutionFuture(future);
        return future;
    }

    // ==================== 核心执行方法 ====================

    protected void executeInternal() {
        List<ChainNode> currentNodes = getStartNodes();
        if (currentNodes == null || currentNodes.isEmpty()) {
            return;
        }

        List<Chain.ExecuteNode> executeNodes = new ArrayList<>();
        for (ChainNode currentNode : currentNodes) {
            executeNodes.add(new Chain.ExecuteNode(currentNode, null, ""));
        }

        doExecuteNodes(executeNodes);
    }

    protected void doExecuteNodes(List<Chain.ExecuteNode> executeNodes) {
        for (Chain.ExecuteNode executeNode : executeNodes) {
            ChainNode currentNode = executeNode.currentNode;
            if (currentNode.isAsync()) {
                chain.getPhaser().register();
                chain.getAsyncNodeExecutors().execute(() -> {
                    try {
                        doExecuteNode(executeNode);
                    } finally {
                        chain.getPhaser().arriveAndDeregister();
                    }
                });
            } else {
                doExecuteNode(executeNode);
            }
        }
    }

    protected void doExecuteNode(Chain.ExecuteNode executeNode) {
        doExecuteNodeInternal(executeNode.currentNode, false);
    }

    /**
     * 统一的节点执行内部方法
     *
     * @param chainNode 节点
     * @param isAsync   是否异步执行
     */
    private void doExecuteNodeInternal(ChainNode chainNode, boolean isAsync) {
        synchronized (chainNode) {
            updateCurrentNodeStatus(chainNode);
            ChainNodeExecuteInfo chainNodeExecuteInfo = chain.getExecuteInfoMap().get(chainNode.getId());
            chainNode.setStatus(chainNodeExecuteInfo.getStatus());

            if (chainNode.getStatus() == ChainNodeStatus.WAIT) {
                return;
            }
            chainNodeExecuteInfo.trigger();

            if (isAsync) {
                notifyNodeStatusChanged(chainNode.getId(), chainNodeExecuteInfo);
            }

            chainNodeExecuteInfo.setStartTime(System.currentTimeMillis());
            chainNodeExecuteInfo.setInwardEdges(chainNode.getInwardEdges().stream()
                    .filter(chainEdge -> chainEdge.getStatus() == ChainEdgeStatus.TRUE)
                    .map(ChainEdge::getId)
                    .collect(Collectors.toList()));

            if (chainNodeExecuteInfo.getStatus() == ChainNodeStatus.READY) {
                Map<String, Object> executeResult = null;
                NodeContext nodeContext = chain.getNodeContext(chainNode);

                try {
                    hooks.onNodeExecuteBefore(nodeContext);

                    chainNodeExecuteInfo.setInputsResult(JSONUtil.toJsonStr(chainNode.getParametersData(chain)));

                    chain.getEventBus().notifyEvent(new NodeStartEvent(chain, chainNode));
                    if (chain.getChainStatus() != ChainStatus.RUNNING) {
                        return;
                    }

                    chainNode.setStatus(ChainNodeStatus.RUNNING);
                    chainNodeExecuteInfo.setStatus(ChainNodeStatus.RUNNING);

                    if (isAsync) {
                        chain.getEventBus().notifyNodeStatusChanged(chain.getId(), chainNode.getId(), chainNodeExecuteInfo);
                    }

                    hooks.onNodeExecuteStart(nodeContext);

                    executeResult = chainNode.execute(chain);
                    chainNodeExecuteInfo.setExecuteResult(JSONUtil.toJsonStr(executeResult));

                    List<com.ymware.engine.domain.value.model.Parameter> outputParameters = chainNode.getOutputParameters();
                    Map<String, Object> outputResult = ParameterResolver.resolve(outputParameters, executeResult);
                    chainNodeExecuteInfo.setOutputResult(JSONUtil.toJsonStr(outputResult));

                    if (!outputResult.isEmpty()) {
                        chain.getMemory().put(chainNode.getId(), new HashMap<>(outputResult));
                    }

                    chainNode.setStatus(ChainNodeStatus.FINISHED);
                    chain.setExecuteResult(executeResult);
                    chain.setOutputResult(outputResult);

                    if (outputResult != null && !outputResult.isEmpty()) {
                        chain.getEventBus().notifyOutput(chain, chainNode, outputResult);
                    }

                } catch (Throwable error) {
                    chainNode.setStatus(ChainNodeStatus.FAILED);
                    log.error("exec {} node {}, error:", chainNode.getNodeType(), chainNode.getId(), error);
                    chainNodeExecuteInfo.setStatus(ChainNodeStatus.FAILED);
                    chainNodeExecuteInfo.setExecuteResult(StrFormatter.format("exec {} node {}, error:", chainNode.getNodeType(), chainNode.getId(), error.getMessage()));
                    chainNodeExecuteInfo.setException(ExceptionUtil.stacktraceToString(error));
                    chain.setChainStatus(ChainStatus.FINISHED_ABNORMAL);
                    if (error instanceof Exception) {
                        chain.setException((Exception) error);
                    } else {
                        chain.setException(new RuntimeException(error));
                    }

                    chain.getEventBus().notifyNodeError(error, chainNode, executeResult, chain);
                } finally {
                    hooks.onNodeExecuteEnd(nodeContext);
                }
            }

            chainNodeExecuteInfo.setStatus(chainNode.getStatus());
            chainNodeExecuteInfo.setEndTime(System.currentTimeMillis());

            if (isAsync) {
                chain.getEventBus().notifyNodeStatusChanged(chain.getId(), chainNode.getId(), chainNodeExecuteInfo);
            }

            NodeContext nodeContext = chain.getNodeContext(chainNode);
            hooks.onNodeExecuteAfter(nodeContext);

            processSubsequentNodes(chainNode, chainNodeExecuteInfo, isAsync);
        }
    }

    // ==================== 后续节点处理 ====================

    private void processSubsequentNodes(ChainNode chainNode, ChainNodeExecuteInfo chainNodeExecuteInfo, boolean isAsync) {
        if (chainNode.getStatus() == ChainNodeStatus.FINISHED) {
            for (ChainEdge outwardEdge : chainNode.getOutwardEdges()) {
                EdgeCondition condition = outwardEdge.getCondition();
                if (condition == null) {
                    outwardEdge.setStatus(ChainEdgeStatus.TRUE);
                    executeNextNode(outwardEdge.getTarget(), isAsync);
                    continue;
                }
                if (condition.check(chain, outwardEdge)) {
                    outwardEdge.setStatus(ChainEdgeStatus.TRUE);
                    chainNodeExecuteInfo.setBranch(outwardEdge.getSourcePortID());
                    if (isAsync) {
                        chain.getEventBus().notifyNodeStatusChanged(chain.getId(), chainNode.getId(), chainNodeExecuteInfo);
                    }
                    executeNextNode(outwardEdge.getTarget(), isAsync);
                } else {
                    outwardEdge.setStatus(ChainEdgeStatus.FALSE);
                    executeNextNode(outwardEdge.getTarget(), isAsync);
                }
            }
        } else if (chainNode.getStatus() == ChainNodeStatus.SKIPPED) {
            for (ChainEdge outwardEdge : chainNode.getOutwardEdges()) {
                outwardEdge.setStatus(ChainEdgeStatus.SKIPPED);
                executeNextNode(outwardEdge.getTarget(), isAsync);
            }
        }
    }

    private void executeNextNode(String targetNodeId, boolean isAsync) {
        ChainNode targetNode = getNodeById(targetNodeId);
        doExecuteNodeInternal(targetNode, true);
    }

    @Deprecated
    private void doExecuteNextNodes(ChainNode currentNode, Map<String, Object> executeResult) {
        List<ChainEdge> outwardEdges = currentNode.getOutwardEdges();
        if (outwardEdges != null && !outwardEdges.isEmpty()) {
            List<Chain.ExecuteNode> nextExecuteNodes = new ArrayList<>(outwardEdges.size());
            for (ChainEdge chainEdge : outwardEdges) {
                ChainNode nextNode = getNodeById(chainEdge.getTarget());
                if (nextNode == null) {
                    continue;
                }
                nextExecuteNodes.add(new Chain.ExecuteNode(nextNode, currentNode, chainEdge.getId()));
            }
            doExecuteNodes(nextExecuteNodes);
        }
    }

    // ==================== 生命周期管理 ====================

    protected void runInLifeCycle(Map<String, Object> variables, ChainEvent startEvent, Runnable runnable) {
        if (variables != null) {
            chain.getMemory().putAll(variables);
        }
        try {
            chain.getEventBus().notifyEvent(startEvent);
            try {
                chain.setStatusAndNotifyEvent(ChainStatus.RUNNING);
                runnable.run();
            } catch (Exception e) {
                log.error("error:", e);
                chain.setException(e);
                chain.setStatusAndNotifyEvent(ChainStatus.ERROR);
                chain.getEventBus().notifyError(e, chain);
            }

            chain.getPhaser().arriveAndAwaitAdvance();

            if (chain.getChainStatus() == ChainStatus.RUNNING) {
                chain.setStatusAndNotifyEvent(ChainStatus.FINISHED_NORMAL);
            } else if (chain.getChainStatus() == ChainStatus.ERROR) {
                chain.setStatusAndNotifyEvent(ChainStatus.FINISHED_ABNORMAL);
            }

        } finally {
            chain.getEventBus().notifyEvent(new ChainEndEvent(chain));
        }
    }

    // ==================== 辅助方法 ====================

    private void updateCurrentNodeStatus(ChainNode chainNode) {
        ChainDepStatus chainDepStatus = ChainDepStatus.calcChainNodeDep(chainNode);
        ChainNodeExecuteInfo chainNodeExecuteInfo = chain.getExecuteInfoMap().computeIfAbsent(chainNode.getId(), id -> {
            ChainNodeExecuteInfo info = new ChainNodeExecuteInfo();
            info.setId(id);
            info.setType(chainNode.getNodeType());
            info.setExecuteInfoId(id + "_" + System.currentTimeMillis() + "_" + id.hashCode());
            return info;
        });
        chainNodeExecuteInfo.setStatus(ChainNodeStatus.fromChainDepStatus(chainDepStatus));
    }

    private List<ChainNode> getStartNodes() {
        List<ChainNode> nodes = chain.getNodes();
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }

        Map<String, ChainNode> suspendNodes = chain.getSuspendNodes();
        if (suspendNodes != null && !suspendNodes.isEmpty()) {
            return new ArrayList<>(suspendNodes.values());
        }

        List<ChainNode> startNodes = new ArrayList<>();
        for (ChainNode node : nodes) {
            if (node.getInwardEdges() == null || node.getInwardEdges().isEmpty()) {
                startNodes.add(node);
            }
        }
        return startNodes;
    }

    private ChainNode getNodeById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return chain.getNodeIndex().get(id);
    }

    // ==================== 异步支持方法 ====================

    private ScheduledFuture<?> startProgressUpdateTask() {
        return chain.getProgressUpdateExecutor().scheduleAtFixedRate(() -> {
            if (!chain.isAsyncExecutionCompleted()) {
                try {
                    int totalNodes = chain.getNodes() != null ? chain.getNodes().size() : 0;
                    int completedNodes = 0;

                    Map<String, ChainNodeExecuteInfo> executeInfoMap = chain.getExecuteInfoMap();
                    if (executeInfoMap != null) {
                        completedNodes = (int) executeInfoMap.values().stream()
                                .filter(info -> info.getStatus() == ChainNodeStatus.FINISHED ||
                                        info.getStatus() == ChainNodeStatus.FAILED ||
                                        info.getStatus() == ChainNodeStatus.SKIPPED)
                                .count();
                    }

                    notifyProgressUpdate(completedNodes, totalNodes);
                } catch (Exception e) {
                    log.warn("进度更新异常", e);
                }
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
    }

    private void waitForCompletion() {
        while (!chain.isAsyncExecutionCompleted() && chain.getChainStatus() != ChainStatus.FINISHED_ABNORMAL) {
            try {
                boolean allCompleted = true;
                List<ChainNode> nodes = chain.getNodes();
                if (nodes != null) {
                    for (ChainNode node : nodes) {
                        ChainNodeStatus status = node.getStatus();
                        if (status == ChainNodeStatus.RUNNING || status == ChainNodeStatus.READY || status == ChainNodeStatus.WAIT) {
                            allCompleted = false;
                            break;
                        }
                    }
                }

                if (allCompleted) {
                    chain.setAsyncExecutionCompleted(true);
                    break;
                }

                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void doExecuteChainNodesAsync(List<ChainNode> list) {
        for (ChainNode chainNode : list) {
            chain.getAsyncNodeExecutors().execute(() -> {
                try {
                    doExecuteChainNodeAsync(chainNode);
                } catch (Exception e) {
                    log.error("异步执行节点失败: {}", chainNode.getId(), e);
                }
            });
        }
    }

    private void doExecuteChainNodeAsync(ChainNode chainNode) {
        doExecuteNodeInternal(chainNode, true);
    }

    // ==================== 通知方法 ====================

    private void notifyExecutionStart() {
        chain.getEventBus().notifyExecutionStart(chain.getId());
    }

    private void notifyNodeStatusChanged(String nodeId, ChainNodeExecuteInfo executeInfo) {
        chain.getEventBus().notifyNodeStatusChanged(chain.getId(), nodeId, executeInfo);
    }

    private void notifyExecutionComplete(Map<String, Object> result, Exception exception) {
        chain.getEventBus().notifyExecutionComplete(chain.getId(), result, exception);
    }

    private void notifyProgressUpdate(int completedNodes, int totalNodes) {
        chain.getEventBus().notifyProgressUpdate(chain.getId(), chain.getExecuteInfoMap(), completedNodes, totalNodes);
    }
}
