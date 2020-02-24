package com.github.mouse0w0.eventbus;

import com.github.mouse0w0.eventbus.reflect.ReflectEventListenerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ReflectEventBusTest {
    private static EventBus eventBus;

    @BeforeAll
    public static void init() {
        eventBus = SimpleEventBus.builder().eventListenerFactory(ReflectEventListenerFactory.instance()).build();
        eventBus.register(new ExampleListener());
    }

    @Test
    public void test() {
        ExampleEvent event = new ExampleEvent();
        eventBus.post(event);
        assert event.isCancelled();
    }
}
