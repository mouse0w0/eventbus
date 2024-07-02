package com.github.mouse0w0.eventbus;

import java.lang.reflect.Type;

final class ListenerWrapper {
    public static final ListenerWrapper[] EMPTY_ARRAY = new ListenerWrapper[0];

    private static final int RECEIVE_CANCELLED = 1;
    private static final int IS_GENERIC = 2;

    private final Class<?> eventType;
    private final Type genericType;
    private final Order order;
    private final ListenerInvoker invoker;
    private final int filterFlags;

    public ListenerWrapper(Class<?> eventType, Type genericType, Order order, boolean receiveCancelled, ListenerInvoker invoker) {
        this.eventType = eventType;
        this.genericType = genericType;
        this.order = order;
        this.invoker = invoker;
        this.filterFlags = (receiveCancelled ? RECEIVE_CANCELLED : 0) | (genericType != null ? IS_GENERIC : 0);
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public Order getOrder() {
        return order;
    }

    public void post(Event event) throws Throwable {
        if (filter(event)) {
            invoker.invoke(event);
        }
    }

    private boolean filter(Event event) {
        switch (filterFlags) {
            case 0:
            default:
                return isNotCancelled(event);
            case RECEIVE_CANCELLED:
                return true;
            case IS_GENERIC:
                return isNotCancelled(event) && isSameGenericType(event);
            case RECEIVE_CANCELLED | IS_GENERIC:
                return isSameGenericType(event);
        }
    }

    private static boolean isNotCancelled(Event event) {
        return !event.isCancellable() || !((Cancellable) event).isCancelled();
    }

    private boolean isSameGenericType(Event event) {
        return ((GenericEvent<?>) event).getGenericType() == genericType;
    }
}
