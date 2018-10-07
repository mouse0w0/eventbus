package com.github.mouse0w0.eventbus;

import com.github.mouse0w0.eventbus.asm.AsmWrappedListenerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsmEventBusTest {

    private static EventBus eventBus;

    @BeforeClass
    public static void init() {
        eventBus = SimpleEventBus.builder().wrappedListenerFactory(AsmWrappedListenerFactory.create()).build();
        eventBus.register(new ExampleListener());
    }

    @Test
    public void test() {
        ExampleEvent event = new ExampleEvent();
        eventBus.post(event);
        assert event.isCancelled();
    }
}
