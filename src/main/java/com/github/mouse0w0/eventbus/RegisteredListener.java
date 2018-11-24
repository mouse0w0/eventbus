package com.github.mouse0w0.eventbus;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class RegisteredListener implements Comparable<RegisteredListener> {

    private final WrappedListener wrappedListener;
    private final Object owner;
    private final Method handler;
    private final Class<?> eventType;
    private final boolean receiveCancelled;
    private final Order order;
    private final Type genericType;

    public RegisteredListener(WrappedListener wrappedListener, Object owner, Method handler, Class<?> eventType, boolean receiveCancelled, Order order) {
        this.wrappedListener = wrappedListener;
        this.owner = owner;
        this.handler = handler;
        this.eventType = eventType;
        this.receiveCancelled = receiveCancelled;
        this.order = order;
        if (GenericEvent.class.isAssignableFrom(eventType)) {
            Type type = handler.getGenericParameterTypes()[0];
            genericType = type instanceof ParameterizedType ? ((ParameterizedType) type).getActualTypeArguments()[0] : null;
        } else {
            genericType = null;
        }
    }

    public Object getOwner() {
        return owner;
    }

    public Method getHandler() {
        return handler;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public boolean isReceiveCancelled() {
        return receiveCancelled;
    }

    public Order getOrder() {
        return order;
    }

    public boolean isGeneric() {
        return genericType != null;
    }

    public Type getGenericType() {
        return genericType;
    }

    public void post(Event event) throws Exception {
        wrappedListener.post(event);
    }

    @Override
    public int compareTo(RegisteredListener o) {
        return getOrder().ordinal() - o.getOrder().ordinal();
    }
}

