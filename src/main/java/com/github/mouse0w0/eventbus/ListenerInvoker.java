package com.github.mouse0w0.eventbus;

public interface ListenerInvoker {
    void invoke(Event event) throws Throwable;
}
