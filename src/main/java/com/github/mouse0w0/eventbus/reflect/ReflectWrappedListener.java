package com.github.mouse0w0.eventbus.reflect;

import com.github.mouse0w0.eventbus.Event;
import com.github.mouse0w0.eventbus.WrappedListener;

import java.lang.reflect.Method;

public class ReflectWrappedListener implements WrappedListener {

    private final Object owner;
    private final Method handler;

    ReflectWrappedListener(Object owner, Method handler) {
        this.owner = owner;
        this.handler = handler;
        handler.setAccessible(true);
    }

    @Override
    public void post(Event event) throws Exception {
        handler.invoke(owner, event);
    }
}
