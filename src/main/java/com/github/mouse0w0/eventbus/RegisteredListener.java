package com.github.mouse0w0.eventbus;

import java.lang.reflect.Method;

public class RegisteredListener implements Comparable<RegisteredListener> {

    private final WrappedListener wrappedListener;
    private final Object owner;
    private final Method handler;
    private final Class<?> eventType;
    private final boolean receiveCancelled;
    private final Order order;

    public RegisteredListener(WrappedListener wrappedListener, Object owner, Method handler, Class<?> eventType, boolean receiveCancelled, Order order) {
        this.wrappedListener = wrappedListener;
        this.owner = owner;
        this.handler = handler;
        this.eventType = eventType;
        this.receiveCancelled = receiveCancelled;
        this.order = order;
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

    public void post(Event event) throws Exception {
        wrappedListener.post(event);
    }

    @Override
    public int compareTo(RegisteredListener o) {
        return getOrder().ordinal() - o.getOrder().ordinal();
    }
}

