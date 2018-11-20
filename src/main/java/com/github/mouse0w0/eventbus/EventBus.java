package com.github.mouse0w0.eventbus;

public interface EventBus {

    /**
     * Handle a event.
     * @return True if cancelled, false if not.
     */
    boolean post(Event event);

    /**
     * Register a listener.
     */
    void register(Object listener);

    /**
     * Unregister a listener.
     */
    void unregister(Object listener);
}
