package com.github.mouse0w0.eventbus;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

public class SimpleEventBus implements EventBus {

    public static Builder builder() {
        return new Builder();
    }

    private final Map<Class<?>, Map<Order, Collection<RegisteredListener>>> eventListeners = new HashMap<>();
    private final Map<Object, Collection<RegisteredListener>> registeredListeners = new HashMap<>();

    private final WrappedListenerFactory wrappedListenerFactory;
    private final ListenerExceptionHandler listenerExceptionHandler;

    public SimpleEventBus(WrappedListenerFactory wrappedListenerFactory, ListenerExceptionHandler listenerExceptionHandler) {
        this.wrappedListenerFactory = wrappedListenerFactory;
        this.listenerExceptionHandler = listenerExceptionHandler;
    }

    @Override
    public boolean post(Event event) {
        Objects.requireNonNull(event, "Event cannot be null");

        Map<Order, Collection<RegisteredListener>> orderedListeners = getListeners(event.getClass());
        if (orderedListeners == null)
            return false;

        if (event.isCancellable()) {
            Cancellable cancellable = (Cancellable) event;

            for (Order order : Order.values()) {
                Collection<RegisteredListener> listeners = orderedListeners.get(order);
                if (listeners == null)
                    continue;

                for (RegisteredListener listener : listeners) {
                    if (!cancellable.isCancelled() || listener.isReceiveCancelled())
                        post(listener, event);
                }
            }
            return cancellable.isCancelled();
        } else {
            for (Order order : Order.values()) {
                Collection<RegisteredListener> listeners = orderedListeners.get(order);
                if (listeners == null)
                    continue;

                for (RegisteredListener listener : listeners) {
                    post(listener, event);
                }
            }
            return false;
        }
    }

    protected Map<Order, Collection<RegisteredListener>> getListeners(Class<?> eventType) {
        Map<Order, Collection<RegisteredListener>> orderedListeners = eventListeners.get(eventType);
        if (orderedListeners != null)
            return orderedListeners;

        orderedListeners = new EnumMap<>(Order.class);
        for (Entry<Class<?>, Map<Order, Collection<RegisteredListener>>> entry : eventListeners.entrySet()) {
            if (!entry.getKey().isAssignableFrom(eventType))
                continue;

            for (Entry<Order, Collection<RegisteredListener>> entry0 : entry.getValue().entrySet()) {
                Collection<RegisteredListener> listeners = orderedListeners.get(entry0.getKey());
                if (listeners == null) {
                    listeners = new LinkedHashSet<>();
                    orderedListeners.put(entry0.getKey(), listeners);
                }
                listeners.addAll(entry0.getValue());
            }
        }
        eventListeners.put(eventType, orderedListeners);

        return orderedListeners;
    }

    protected void post(RegisteredListener listener, Event event) {
        try {
            if (event instanceof GenericEvent && listener.isGeneric()) {
                if (((GenericEvent) event).getGenericType() == listener.getGenericType()) {
                    listener.post(event);
                }
            } else {
                listener.post(event);
            }
        } catch (Exception e) {
            handleListenerException(listener, event, e);
        }
    }

    protected void handleListenerException(RegisteredListener listener, Event event, Exception exception) {
        if (listenerExceptionHandler != null)
            listenerExceptionHandler.handle(listener, event, exception);
    }

    @Override
    public void register(Object listener) {
        Objects.requireNonNull(listener, "Listener cannot be null");

        if (registeredListeners.containsKey(listener))
            throw new EventException("Listener has been registered.");

        Collection<RegisteredListener> listenerExecutors = new LinkedList<>();

        Class<?> clazz = listener.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            Listener anno = method.getAnnotation(Listener.class);
            if (anno == null)
                continue;

            int modifiers = method.getModifiers();

            if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers) || Modifier.isAbstract(modifiers)) {
                throw new EventException(String.format("Require event bus listened method is public and not static/abstract! Source: %s.%s", clazz.getName(), method.getName())); // TODO: support static
            }

            if (method.getParameterCount() != 1) {
                throw new EventException(String.format("Require event bus listened method has only one event parameter! Source: %s.%s", clazz.getName(), method.getName()));
            }

            Class<?> eventType = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(eventType)) {
                throw new EventException(String.format("Require event bus listened method has only one event parameter! Source: %s.%s", clazz.getName(), method.getName()));
            }

            try {
                WrappedListener wrappedListener = wrappedListenerFactory.create(listener, method, eventType);
                RegisteredListener registeredListener = new RegisteredListener(wrappedListener, listener, method, eventType, anno.receiveCancelled(), anno.order());
                listenerExecutors.add(registeredListener);
                addEventListener(eventType, registeredListener);
            } catch (Exception e) {
                throw new EventException(String.format("Cannot create listener wrapper. Source: %s.%s", clazz.getName(), method.getName()));
            }

        }

        registeredListeners.put(listener, listenerExecutors);
    }

    private void addEventListener(Class<?> eventType, RegisteredListener listener) {
        for (Entry<Class<?>, Map<Order, Collection<RegisteredListener>>> entry : eventListeners.entrySet()) {
            Class<?> childEventType = entry.getKey();
            if (!eventType.isAssignableFrom(childEventType))
                continue;

            addEventListener(entry.getValue(), listener);
        }

        if (!eventListeners.containsKey(eventType)) {
            Map<Order, Collection<RegisteredListener>> orderedListeners = new EnumMap<>(Order.class);
            eventListeners.put(eventType, orderedListeners);
            addEventListener(orderedListeners, listener);
        }
    }

    private void addEventListener(Map<Order, Collection<RegisteredListener>> orderedListeners,
                                  RegisteredListener listener) {
        Collection<RegisteredListener> listeners = orderedListeners.get(listener.getOrder());
        if (listeners == null) {
            listeners = new LinkedHashSet<>();
            orderedListeners.put(listener.getOrder(), listeners);
        }
        listeners.add(listener);
    }

    @Override
    public void unregister(Object listener) {
        Objects.requireNonNull(listener, "Listener cannot be null");

        Collection<RegisteredListener> executors = registeredListeners.get(listener);
        if (executors == null)
            return;

        for (RegisteredListener executor : executors) {
            Class<?> eventType = executor.getEventType();
            for (Entry<Class<?>, Map<Order, Collection<RegisteredListener>>> entry : eventListeners.entrySet()) {
                Class<?> childEventType = entry.getKey();
                if (!eventType.isAssignableFrom(childEventType))
                    continue;

                entry.getValue().get(executor.getOrder()).remove(executor);
            }
        }
    }

    public static class Builder {
        private WrappedListenerFactory wrappedListenerFactory;
        private ListenerExceptionHandler listenerExceptionHandler;

        private Builder() {
        }

        public Builder wrappedListenerFactory(WrappedListenerFactory wrappedListenerFactory) {
            this.wrappedListenerFactory = wrappedListenerFactory;
            return this;
        }

        public Builder listenerExceptionHandler(ListenerExceptionHandler listenerExceptionHandler) {
            this.listenerExceptionHandler = listenerExceptionHandler;
            return this;
        }

        public SimpleEventBus build() {
            return new SimpleEventBus(wrappedListenerFactory, listenerExceptionHandler);
        }
    }
}
