package com.github.mouse0w0.eventbus;

import java.util.function.Consumer;

public class ConsumerListenerInvoker<T extends Event> implements ListenerInvoker {
    private final Consumer<T> consumer;

    public ConsumerListenerInvoker(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Event event) throws Throwable {
        consumer.accept((T) event);
    }
}
