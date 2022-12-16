package com.github.mouse0w0.eventbus;

interface ListenerInvoker {
    void invoke(Event event) throws Throwable;
}
