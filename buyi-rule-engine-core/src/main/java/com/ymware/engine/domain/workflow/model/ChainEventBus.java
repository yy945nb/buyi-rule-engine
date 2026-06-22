package com.ymware.engine.domain.workflow.model;

import com.ymware.engine.domain.workflow.event.*;
import com.ymware.engine.domain.workflow.listener.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 工作流事件总线 - 负责所有监听器管理和事件通知
 * 从 Chain 中提取，遵循单一职责原则
 */
@Slf4j
public class ChainEventBus {

    private final Map<Class<?>, List<ChainEventListener>> eventListeners = new java.util.concurrent.ConcurrentHashMap<>();
    private final List<ChainOutputListener> outputListeners = new CopyOnWriteArrayList<>();
    private final List<ChainErrorListener> chainErrorListeners = new CopyOnWriteArrayList<>();
    private final List<NodeErrorListener> nodeErrorListeners = new CopyOnWriteArrayList<>();
    private final List<ChainSuspendListener> suspendListeners = new CopyOnWriteArrayList<>();
    private final List<ChainExecutionListener> executionListeners = new CopyOnWriteArrayList<>();

    private Chain parent;

    public void setParent(Chain parent) {
        this.parent = parent;
    }

    // ==================== 事件监听器管理 ====================

    public void addEventListener(Class<? extends ChainEvent> eventClass, ChainEventListener listener) {
        eventListeners.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public void addEventListener(ChainEventListener listener) {
        eventListeners.computeIfAbsent(ChainEvent.class, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public void removeEventListener(ChainEventListener listener) {
        for (List<ChainEventListener> list : eventListeners.values()) {
            list.removeIf(item -> item == listener);
        }
    }

    public void removeEventListener(Class<? extends ChainEvent> eventClass, ChainEventListener listener) {
        List<ChainEventListener> list = eventListeners.get(eventClass);
        if (list != null) {
            list.removeIf(item -> item == listener);
        }
    }

    public void addErrorListener(ChainErrorListener listener) {
        this.chainErrorListeners.add(listener);
    }

    public void removeErrorListener(ChainErrorListener listener) {
        this.chainErrorListeners.remove(listener);
    }

    public void addNodeErrorListener(NodeErrorListener listener) {
        this.nodeErrorListeners.add(listener);
    }

    public void removeNodeErrorListener(NodeErrorListener listener) {
        this.nodeErrorListeners.remove(listener);
    }

    public void addSuspendListener(ChainSuspendListener listener) {
        this.suspendListeners.add(listener);
    }

    public void removeSuspendListener(ChainSuspendListener listener) {
        this.suspendListeners.remove(listener);
    }

    public void addOutputListener(ChainOutputListener outputListener) {
        this.outputListeners.add(outputListener);
    }

    public void addExecutionListener(ChainExecutionListener listener) {
        if (listener != null) {
            this.executionListeners.add(listener);
        }
    }

    public void removeExecutionListener(ChainExecutionListener listener) {
        this.executionListeners.remove(listener);
    }

    public List<ChainExecutionListener> getExecutionListeners() {
        return executionListeners;
    }

    // ==================== 事件通知 ====================

    public void notifyEvent(ChainEvent event) {
        for (Map.Entry<Class<?>, List<ChainEventListener>> entry : eventListeners.entrySet()) {
            if (entry.getKey().isInstance(event)) {
                for (ChainEventListener chainEventListener : entry.getValue()) {
                    try {
                        chainEventListener.onEvent(event);
                    } catch (Exception e) {
                        log.warn("事件监听器通知异常", e);
                    }
                }
            }
        }
        if (parent != null) {
            parent.getEventBus().notifyEvent(event);
        }
    }

    public void notifyOutput(Chain chain, ChainNode node, Object response) {
        for (ChainOutputListener inputListener : outputListeners) {
            try {
                inputListener.onOutput(chain, node, response);
            } catch (Exception e) {
                log.warn("输出监听器通知异常", e);
            }
        }
        if (parent != null) {
            parent.getEventBus().notifyOutput(chain, node, response);
        }
    }

    public void notifySuspend(Chain chain) {
        for (ChainSuspendListener suspendListener : suspendListeners) {
            try {
                suspendListener.onSuspend(chain);
            } catch (Exception e) {
                log.warn("暂停监听器通知异常", e);
            }
        }
        if (parent != null) {
            parent.getEventBus().notifySuspend(chain);
        }
    }

    public void notifyError(Throwable error, Chain chain) {
        for (ChainErrorListener errorListener : chainErrorListeners) {
            try {
                errorListener.onError(error, chain);
            } catch (Exception e) {
                log.warn("错误监听器通知异常", e);
            }
        }
        if (parent != null) {
            parent.getEventBus().notifyError(error, chain);
        }
    }

    public void notifyNodeError(Throwable error, ChainNode node, Map<String, Object> executeResult, Chain chain) {
        for (NodeErrorListener errorListener : nodeErrorListeners) {
            try {
                errorListener.onError(error, node, executeResult, chain);
            } catch (Exception e) {
                log.warn("节点错误监听器通知异常", e);
            }
        }
        if (parent != null) {
            parent.getEventBus().notifyNodeError(error, node, executeResult, chain);
        }
    }

    // ==================== 执行监听器通知 ====================

    public void notifyExecutionStart(String chainId) {
        for (ChainExecutionListener listener : executionListeners) {
            try {
                listener.onExecutionStart(chainId);
            } catch (Exception e) {
                log.warn("监听器通知异常", e);
            }
        }
    }

    public void notifyNodeStatusChanged(String chainId, String nodeId, ChainNodeExecuteInfo executeInfo) {
        if (parent != null) {
            for (ChainExecutionListener listener : parent.getEventBus().getExecutionListeners()) {
                try {
                    listener.onNodeStatusChanged(chainId, nodeId, executeInfo);
                } catch (Exception e) {
                    log.warn("监听器通知异常", e);
                }
            }
        }
        for (ChainExecutionListener listener : executionListeners) {
            try {
                listener.onNodeStatusChanged(chainId, nodeId, executeInfo);
            } catch (Exception e) {
                log.warn("监听器通知异常", e);
            }
        }
    }

    public void notifyExecutionComplete(String chainId, Map<String, Object> result, Exception exception) {
        for (ChainExecutionListener listener : executionListeners) {
            try {
                listener.onExecutionComplete(chainId, result, exception);
            } catch (Exception e) {
                log.warn("监听器通知异常", e);
            }
        }
    }

    public void notifyProgressUpdate(String chainId, Map<String, ChainNodeExecuteInfo> executeInfoMap,
                                     int completedNodes, int totalNodes) {
        for (ChainExecutionListener listener : executionListeners) {
            try {
                listener.onProgressUpdate(chainId, executeInfoMap, completedNodes, totalNodes);
            } catch (Exception e) {
                log.warn("监听器通知异常", e);
            }
        }
    }
}
