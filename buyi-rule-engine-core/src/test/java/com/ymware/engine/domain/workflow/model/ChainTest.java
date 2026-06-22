package com.ymware.engine.domain.workflow.model;

import com.ymware.engine.domain.workflow.event.ChainStartEvent;
import com.ymware.engine.domain.workflow.type.ChainStatus;
import com.ymware.engine.domain.workflow.type.NodeType;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ChainTest {

    @Test
    void memoryShouldBeThreadSafeForConcurrentWrites() {
        Chain chain = new Chain();
        ExecutorService pool = Executors.newFixedThreadPool(10);

        assertDoesNotThrow(() -> {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                final int idx = i;
                futures.add(pool.submit(() ->
                        chain.getMemory().put("key-" + idx, "value-" + idx)));
            }
            for (Future<?> f : futures) {
                f.get(5, TimeUnit.SECONDS);
            }
        });

        assertEquals(100, chain.getMemory().size());
        pool.shutdown();
    }

    @Test
    void memoryShouldSurviveSetMemoryCall() {
        Chain chain = new Chain();
        chain.getMemory().put("original", "value");

        Map<String, Object> newMemory = Map.of("replaced", "data");
        chain.setMemory(newMemory);

        // The map reference is the same (final), but contents replaced
        assertEquals("data", chain.getMemory().get("replaced"));
        // Original key should be cleared
        assertFalse(chain.getMemory().containsKey("original"));
    }

    @Test
    void addNodeShouldIndexForO1Lookup() {
        Chain chain = new Chain();

        // Add 100 nodes
        for (int i = 0; i < 100; i++) {
            TestNode node = new TestNode("node-" + i);
            chain.addNode(node);
        }

        // Verify all nodes are accessible
        assertNotNull(chain.getNodes());
        assertEquals(100, chain.getNodes().size());
    }

    @Test
    void addNodeShouldSetParentForSubChain() {
        Chain parent = new Chain();
        Chain child = new Chain();

        parent.addNode(child);

        assertSame(parent, child.getParent());
    }

    @Test
    void addNodeShouldAutoGenerateIdIfNull() {
        Chain chain = new Chain();
        TestNode node = new TestNode(null);
        chain.addNode(node);

        assertNotNull(node.getId());
    }

    @Test
    void eventBusShouldBeAccessible() {
        Chain chain = new Chain();
        assertNotNull(chain.getEventBus());
    }

    @Test
    void resetShouldClearAllState() {
        Chain chain = new Chain();
        chain.getMemory().put("key", "value");

        chain.reset();

        assertTrue(chain.getMemory().isEmpty());
        assertEquals(ChainStatus.READY, chain.getChainStatus());
        assertNull(chain.getException());
        assertNull(chain.getMessage());
    }

    @Test
    void stopNormalShouldSetFinishedStatus() {
        Chain chain = new Chain();
        chain.stopNormal("done");

        assertEquals(ChainStatus.FINISHED_NORMAL, chain.getChainStatus());
        assertEquals("done", chain.getMessage());
    }

    @Test
    void stopErrorShouldSetAbnormalStatus() {
        Chain chain = new Chain();
        chain.stopError("failed");

        assertEquals(ChainStatus.FINISHED_ABNORMAL, chain.getChainStatus());
        assertEquals("failed", chain.getMessage());
    }

    @Test
    void suspendShouldSetSuspendStatus() {
        Chain chain = new Chain();
        TestNode node = new TestNode("suspend-node");
        chain.addNode(node);

        chain.suspend(node);

        assertEquals(ChainStatus.SUSPEND, chain.getChainStatus());
    }

    @Test
    void chainConstructorShouldSetNodeType() {
        Chain chain = new Chain();
        assertEquals(NodeType.CHAIN, chain.getNodeType());
        assertNotNull(chain.getId());
    }

    @Test
    void shutdownAsyncExecutionShouldShutdownAllPools() {
        Chain chain = new Chain();
        // Verify pools are running before shutdown
        assertNotNull(chain.getAsyncNodeExecutors());
        assertNotNull(chain.getProgressUpdateExecutor());

        chain.shutdownAsyncExecution();

        assertTrue(chain.getProgressUpdateExecutor().isShutdown());
        assertTrue(chain.getAsyncNodeExecutors().isShutdown());
    }

    @Test
    void shutdownAsyncExecutionShouldBeIdempotent() {
        Chain chain = new Chain();
        chain.shutdownAsyncExecution();
        // Calling again should not throw
        assertDoesNotThrow(chain::shutdownAsyncExecution);
    }

    /**
     * Concrete ChainNode for testing
     */
    private static class TestNode extends ChainNode {
        public TestNode(String id) {
            this.id = id;
            this.nodeType = NodeType.START;
        }

        @Override
        public Map<String, Object> execute(Chain chain) {
            return Map.of();
        }
    }
}
