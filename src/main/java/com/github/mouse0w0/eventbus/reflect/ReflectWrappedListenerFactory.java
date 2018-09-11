package com.github.mouse0w0.eventbus.reflect;

import com.github.mouse0w0.eventbus.WrappedListener;
import com.github.mouse0w0.eventbus.WrappedListenerFactory;

import java.lang.reflect.Method;

public class ReflectWrappedListenerFactory implements WrappedListenerFactory {

    public static final ReflectWrappedListenerFactory INSTANCE = new ReflectWrappedListenerFactory();

    @Override
    public WrappedListener create(Object owner, Method handler, Class<?> eventType) throws Exception {
        return new ReflectWrappedListener(owner, handler);
    }
}
