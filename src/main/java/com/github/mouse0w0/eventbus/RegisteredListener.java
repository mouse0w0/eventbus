package com.github.mouse0w0.eventbus;

public class RegisteredListener implements Comparable<RegisteredListener> {

    private final WrappedListener wrappedListener;
    private final Object owner;
    private final Class<?> eventType;
    private final boolean receiveCancelled;
    private final Order order;

    public RegisteredListener(WrappedListener wrappedListener, Object owner, Class<?> eventType, boolean receiveCancelled, Order order) {
        this.wrappedListener = wrappedListener;
        this.owner = owner;
        this.eventType = eventType;
        this.receiveCancelled = receiveCancelled;
        this.order = order;
    }

    public Object getOwner() {
        return owner;
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

