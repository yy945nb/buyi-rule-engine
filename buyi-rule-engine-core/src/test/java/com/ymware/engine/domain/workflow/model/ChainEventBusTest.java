package com.ymware.engine.domain.workflow.model;

import com.ymware.engine.domain.workflow.event.*;
import com.ymware.engine.domain.workflow.listener.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ChainEventBusTest {

    private ChainEventBus eventBus;

    @BeforeEach
    void setUp() {
        eventBus = new ChainEventBus();
    }

    @Test
    void notifyEventShouldDeliverToRegisteredListener() {
        AtomicReference<ChainEvent> received = new AtomicReference<>();
        eventBus.addEventListener(event -> received.set(event));

        Chain chain = new Chain();
        ChainStartEvent event = new ChainStartEvent(chain, Map.of());
        eventBus.notifyEvent(event);

        assertNotNull(received.get());
        assertSame(event, received.get());
    }

    @Test
    void notifyEventShouldDeliverToTypedListener() {
        AtomicReference<ChainEvent> received = new AtomicReference<>();
        eventBus.addEventListener(ChainStartEvent.class, event -> received.set(event));

        Chain chain = new Chain();
        ChainStartEvent event = new ChainStartEvent(chain, Map.of());
        eventBus.notifyEvent(event);

        assertNotNull(received.get());
    }

    @Test
    void notifyEventShouldNotDeliverToWrongTypedListener() {
        AtomicReference<ChainEvent> received = new AtomicReference<>();
        eventBus.addEventListener(ChainEndEvent.class, event -> received.set(event));

        Chain chain = new Chain();
        ChainStartEvent event = new ChainStartEvent(chain, Map.of());
        eventBus.notifyEvent(event);

        assertNull(received.get());
    }

    @Test
    void removeEventListenerShouldStopDelivery() {
        AtomicInteger count = new AtomicInteger(0);
        ChainEventListener listener = event -> count.incrementAndGet();

        eventBus.addEventListener(listener);
        eventBus.notifyEvent(new ChainStartEvent(new Chain(), Map.of()));
        assertEquals(1, count.get());

        eventBus.removeEventListener(listener);
        eventBus.notifyEvent(new ChainStartEvent(new Chain(), Map.of()));
        assertEquals(1, count.get());
    }

    @Test
    void notifyOutputShouldDeliverToListener() {
        AtomicReference<Object> received = new AtomicReference<>();
        eventBus.addOutputListener((chain, node, output) -> received.set(output));

        Chain chain = new Chain();
        eventBus.notifyOutput(chain, null, "test-output");

        assertEquals("test-output", received.get());
    }

    @Test
    void notifyErrorShouldDeliverToListener() {
        AtomicReference<Throwable> received = new AtomicReference<>();
        eventBus.addErrorListener((error, chain) -> received.set(error));

        RuntimeException error = new RuntimeException("test");
        eventBus.notifyError(error, new Chain());

        assertSame(error, received.get());
    }

    @Test
    void notifyNodeErrorShouldDeliverToListener() {
        AtomicReference<Throwable> received = new AtomicReference<>();
        eventBus.addNodeErrorListener((error, node, result, chain) -> received.set(error));

        RuntimeException error = new RuntimeException("node-error");
        eventBus.notifyNodeError(error, new Chain(), Map.of(), new Chain());

        assertSame(error, received.get());
    }

    @Test
    void notifySuspendShouldDeliverToListener() {
        AtomicInteger count = new AtomicInteger(0);
        eventBus.addSuspendListener(chain -> count.incrementAndGet());

        eventBus.notifySuspend(new Chain());
        assertEquals(1, count.get());
    }

    @Test
    void notifyExecutionStartShouldDeliverToListener() {
        AtomicReference<String> received = new AtomicReference<>();
        eventBus.addExecutionListener(new ChainExecutionListener() {
            @Override
            public void onExecutionStart(String chainId) {
                received.set(chainId);
            }
        });

        eventBus.notifyExecutionStart("chain-1");
        assertEquals("chain-1", received.get());
    }

    @Test
    void notifyNodeStatusChangedShouldDeliverToListener() {
        AtomicReference<String> receivedNodeId = new AtomicReference<>();
        eventBus.addExecutionListener(new ChainExecutionListener() {
            @Override
            public void onNodeStatusChanged(String chainId, String nodeId, ChainNodeExecuteInfo executeInfo) {
                receivedNodeId.set(nodeId);
            }
        });

        eventBus.notifyNodeStatusChanged("chain-1", "node-1", new ChainNodeExecuteInfo());
        assertEquals("node-1", receivedNodeId.get());
    }

    @Test
    void listenerExceptionShouldNotBreakNotification() {
        // First listener throws
        eventBus.addEventListener(event -> {
            throw new RuntimeException("listener error");
        });
        // Second listener should still receive the event
        AtomicInteger count = new AtomicInteger(0);
        eventBus.addEventListener(event -> count.incrementAndGet());

        eventBus.notifyEvent(new ChainStartEvent(new Chain(), Map.of()));
        assertEquals(1, count.get());
    }

    @Test
    void parentChainShouldReceiveEvents() {
        AtomicInteger parentCount = new AtomicInteger(0);
        Chain parent = new Chain();
        parent.getEventBus().addEventListener(event -> parentCount.incrementAndGet());

        Chain child = new Chain();
        child.getEventBus().setParent(parent);

        child.getEventBus().notifyEvent(new ChainStartEvent(child, Map.of()));
        assertEquals(1, parentCount.get());
    }
}
