package com.github.mouse0w0.eventbus;

import com.github.mouse0w0.eventbus.asm.AsmWrappedListenerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsmEventBusTest {

    private static EventBus eventBus;
    private static ExampleGenericListener genericListener;

    @BeforeClass
    public static void init() {
        eventBus = SimpleEventBus.builder().wrappedListenerFactory(AsmWrappedListenerFactory.create()).build();
        eventBus.register(new ExampleListener());

        genericListener = new ExampleGenericListener();
        eventBus.register(genericListener);
    }

    @Test
    public void test() {
        ExampleEvent event = new ExampleEvent();
        eventBus.post(event);
        eventBus.post(new ExampleGenericEvent<>(String.class));
        assert event.isCancelled();
        assert genericListener.normalTestDone;
        assert genericListener.genericTestDone;
    }
}
