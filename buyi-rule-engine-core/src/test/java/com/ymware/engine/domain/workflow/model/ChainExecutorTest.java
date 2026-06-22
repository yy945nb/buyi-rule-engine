package com.ymware.engine.domain.workflow.model;

import com.ymware.engine.domain.workflow.type.ChainNodeStatus;
import com.ymware.engine.domain.workflow.type.ChainStatus;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ChainExecutorTest {

    private static ChainNode createSimpleNode(String id) {
        return new ChainNode() {
            @Override
            public Map<String, Object> execute(Chain chain) {
                Map<String, Object> result = new HashMap<>();
                result.put("output", "value_" + id);
                return result;
            }
        };
    }

    @Test
    void executeRunsStartNodes() {
        Chain chain = new Chain();
        ChainNode node = createSimpleNode("n1");
        node.setId("n1");
        chain.addNode(node);

        chain.execute(new HashMap<>());

        assertEquals(ChainStatus.FINISHED_NORMAL, chain.getChainStatus());
    }

    @Test
    void executeForResultReturnsOutput() {
        Chain chain = new Chain();
        ChainNode node = createSimpleNode("n1");
        node.setId("n1");
        chain.addNode(node);

        chain.executeForResult(new HashMap<>());

        assertEquals(ChainStatus.FINISHED_NORMAL, chain.getChainStatus());
    }

    @Test
    void executeWithEmptyChainCompletes() {
        Chain chain = new Chain();
        chain.execute(new HashMap<>());
        assertEquals(ChainStatus.FINISHED_NORMAL, chain.getChainStatus());
    }

    @Test
    void hooksCalledInOrder() {
        List<String> hookOrder = Collections.synchronizedList(new ArrayList<>());

        Chain chain = new Chain() {
            @Override
            protected void onNodeExecuteBefore(NodeContext ctx) { hookOrder.add("before"); }
            @Override
            protected void onNodeExecuteStart(NodeContext ctx) { hookOrder.add("start"); }
            @Override
            protected void onNodeExecuteEnd(NodeContext ctx) { hookOrder.add("end"); }
            @Override
            protected void onNodeExecuteAfter(NodeContext ctx) { hookOrder.add("after"); }
        };

        ChainNode node = createSimpleNode("n1");
        node.setId("n1");
        chain.addNode(node);

        chain.execute(new HashMap<>());

        assertEquals(4, hookOrder.size());
        assertEquals("before", hookOrder.get(0));
        assertEquals("start", hookOrder.get(1));
        assertEquals("end", hookOrder.get(2));
        assertEquals("after", hookOrder.get(3));
    }

    @Test
    void resetClearsExecutor() {
        Chain chain = new Chain();
        ChainNode node = createSimpleNode("n1");
        node.setId("n1");
        chain.addNode(node);

        chain.execute(new HashMap<>());
        assertEquals(ChainStatus.FINISHED_NORMAL, chain.getChainStatus());

        chain.reset();
        assertEquals(ChainStatus.READY, chain.getChainStatus());
        assertNull(chain.getException());
        assertNull(chain.getExecuteResult());
    }

    @Test
    void chainedNodesExecuteInOrder() {
        List<String> executionOrder = Collections.synchronizedList(new ArrayList<>());

        Chain chain = new Chain();
        ChainNode node1 = new ChainNode() {
            @Override
            public Map<String, Object> execute(Chain c) {
                executionOrder.add("n1");
                return Map.of("step", 1);
            }
        };
        node1.setId("n1");

        ChainNode node2 = new ChainNode() {
            @Override
            public Map<String, Object> execute(Chain c) {
                executionOrder.add("n2");
                return Map.of("step", 2);
            }
        };
        node2.setId("n2");

        chain.addNode(node1);
        chain.addNode(node2);

        ChainEdge edge = new ChainEdge();
        edge.setId("e1");
        edge.setSource("n1");
        edge.setTarget("n2");
        chain.addEdge(edge);

        chain.execute(new HashMap<>());

        assertEquals(ChainStatus.FINISHED_NORMAL, chain.getChainStatus());
        assertEquals(2, executionOrder.size());
        assertEquals("n1", executionOrder.get(0));
        assertEquals("n2", executionOrder.get(1));
    }

    @Test
    void nodeFailureSetsChainAbnormal() {
        Chain chain = new Chain();
        ChainNode node = new ChainNode() {
            @Override
            public Map<String, Object> execute(Chain c) {
                throw new RuntimeException("test error");
            }
        };
        node.setId("n1");
        chain.addNode(node);

        assertThrows(RuntimeException.class, () -> chain.executeForResult(new HashMap<>()));
        assertEquals(ChainStatus.FINISHED_ABNORMAL, chain.getChainStatus());
    }

    @Test
    void ignoreErrorDoesNotThrow() {
        Chain chain = new Chain();
        ChainNode node = new ChainNode() {
            @Override
            public Map<String, Object> execute(Chain c) {
                throw new RuntimeException("test error");
            }
        };
        node.setId("n1");
        chain.addNode(node);

        assertDoesNotThrow(() -> chain.executeForResult(new HashMap<>(), true));
        assertEquals(ChainStatus.FINISHED_ABNORMAL, chain.getChainStatus());
    }

    @Test
    void asyncExecutionCompletes() throws Exception {
        Chain chain = new Chain();
        ChainNode node = createSimpleNode("n1");
        node.setId("n1");
        chain.addNode(node);

        CompletableFuture<Map<String, Object>> future = chain.executeAsync(new HashMap<>());
        Map<String, Object> result = future.get();

        assertNotNull(result);
    }

    @Test
    void executorHooksDelegateToChainSubclass() {
        AtomicBoolean beforeCalled = new AtomicBoolean(false);
        AtomicBoolean afterCalled = new AtomicBoolean(false);

        Chain chain = new Chain() {
            @Override
            protected void onNodeExecuteBefore(NodeContext ctx) { beforeCalled.set(true); }
            @Override
            protected void onNodeExecuteAfter(NodeContext ctx) { afterCalled.set(true); }
        };

        ChainNode node = createSimpleNode("n1");
        node.setId("n1");
        chain.addNode(node);

        chain.execute(new HashMap<>());

        assertTrue(beforeCalled.get());
        assertTrue(afterCalled.get());
    }
}
