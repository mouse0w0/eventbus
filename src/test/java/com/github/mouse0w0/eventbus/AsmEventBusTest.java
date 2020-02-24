package com.github.mouse0w0.eventbus;

import com.github.mouse0w0.eventbus.asm.AsmEventListenerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AsmEventBusTest {

    private static EventBus eventBus;
    private static ExampleListener listener;
    private static ExampleGenericListener genericListener;

    @BeforeAll
    public static void init() {
        eventBus = SimpleEventBus.builder().eventListenerFactory(AsmEventListenerFactory.create()).build();
        listener = new ExampleListener();
        eventBus.register(listener);

        genericListener = new ExampleGenericListener();
        eventBus.register(genericListener);
    }

    @Test
    public void test() {
        ExampleEvent event = new ExampleEvent();
        eventBus.post(event);
        eventBus.unregister(listener);
        eventBus.post(new ExampleGenericEvent<>(String.class));
        assert event.isCancelled();
        assert genericListener.normalTestDone;
        assert genericListener.genericTestDone;
    }
}
