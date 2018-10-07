package com.github.mouse0w0.eventbus;

import com.github.mouse0w0.eventbus.reflect.ReflectWrappedListenerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReflectEventBusTest {
    private static EventBus eventBus;

    @BeforeClass
    public static void init() {
        eventBus = SimpleEventBus.builder().wrappedListenerFactory(ReflectWrappedListenerFactory.INSTANCE).build();
        eventBus.register(new ExampleListener());
    }

    @Test
    public void test() {
        ExampleEvent event = new ExampleEvent();
        eventBus.post(event);
        assert event.isCancelled();
    }
}
