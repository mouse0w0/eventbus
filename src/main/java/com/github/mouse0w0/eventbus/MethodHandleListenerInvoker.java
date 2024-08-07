package com.github.mouse0w0.eventbus;

import java.lang.invoke.MethodHandle;

public class MethodHandleListenerInvoker implements ListenerInvoker {
    private final MethodHandle handle;

    public MethodHandleListenerInvoker(MethodHandle handle) {
        this.handle = handle;
    }

    @Override
    public void invoke(Event event) throws Throwable {
        handle.invoke(event);
    }
}
