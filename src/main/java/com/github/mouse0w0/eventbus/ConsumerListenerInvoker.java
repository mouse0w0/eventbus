package com.github.mouse0w0.eventbus;

import java.util.function.Consumer;

public class ConsumerListenerInvoker<T extends Event> implements ListenerInvoker {
    private final Class<T> eventType;
    private final Consumer<T> consumer;

    public ConsumerListenerInvoker(Class<T> eventType, Consumer<T> consumer) {
        this.eventType = eventType;
        this.consumer = consumer;
    }

    @Override
    public void invoke(Event event) throws Throwable {
        consumer.accept(eventType.cast(event));
    }
}
