package com.github.mouse0w0.eventbus;

public class ExampleEvent implements Event, Cancellable {

    private Order currentState = Order.FIRST;

    public Order getCurrentState() {
        return currentState;
    }

    public void setCurrentState(Order currentState) {
        this.currentState = currentState;
    }

    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
