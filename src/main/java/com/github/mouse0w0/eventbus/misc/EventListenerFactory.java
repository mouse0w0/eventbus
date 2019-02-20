package com.github.mouse0w0.eventbus.misc;

import java.lang.reflect.Method;

@FunctionalInterface
public interface EventListenerFactory {

    EventListener create(Class<?> eventType, Object owner, Method method, boolean isStatic) throws Exception;
}
