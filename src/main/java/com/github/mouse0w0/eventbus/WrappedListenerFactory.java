package com.github.mouse0w0.eventbus;

import java.lang.reflect.Method;

public interface WrappedListenerFactory {

    WrappedListener create(Object owner, Method handler, Class<?> eventType) throws Exception;
}
