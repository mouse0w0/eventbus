package com.github.mouse0w0.eventbus;

import java.util.function.Consumer;

public interface EventBus {

    /**
     * Handle a event.
     *
     * @param event The event
     * @return True if cancelled, false if not.
     */
    boolean post(Event event);

    /**
     * Register listeners.
     * @param target The listener
     */
    void register(Object target);

    /**
     * Unregister listeners.
     * @param target The listener
     */
    void unregister(Object target);

    <T extends Event> void addListener(Consumer<T> consumer);

    <T extends Event> void addListener(Order order, Consumer<T> consumer);

    <T extends Event> void addListener(Order order, boolean receiveCancelled, Consumer<T> consumer);

    <T extends Event> void addListener(Order order, boolean receiveCancelled, Class<T> eventType, Consumer<T> consumer);

    <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Consumer<T> consumer);

    <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, Consumer<T> consumer);

    <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, boolean receiveCancelled, Consumer<T> consumer);

    <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, boolean receiveCancelled, Class<T> eventType, Consumer<T> consumer);
}
