package com.github.mouse0w0.eventbus;

@FunctionalInterface
public interface ListenerExceptionHandler {

    void handle(RegisteredListener listener, Event event, Exception exception);
}
