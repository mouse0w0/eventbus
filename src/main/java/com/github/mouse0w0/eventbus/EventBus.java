package com.github.mouse0w0.eventbus;

public interface EventBus {

    /**
     * @param event
     * @return True if cancelled, false if not.
     */
    boolean post(Event event);

    void register(Object listener);

    void unregister(Object listener);
}
