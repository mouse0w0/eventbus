package com.github.mouse0w0.eventbus;

import net.jodah.typetools.TypeResolver;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleEventBus implements EventBus {
    private final Map<Class<?>, ListenerList> listenerLists = new HashMap<>();
    private final Map<Object, ListenerWrapper[]> ownerToListeners = new HashMap<>();
    private final EventExceptionHandler exceptionHandler;

    public SimpleEventBus() {
        this.exceptionHandler = EventExceptionHandler.PRINT;
    }

    public SimpleEventBus(EventExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public boolean post(Event event) {
        ListenerList listenerList = getListenerList(event.getClass());
        for (ListenerWrapper listener : listenerList) {
            try {
                listener.post(event);
            } catch (Throwable t) {
                exceptionHandler.handle(event, t);
            }
        }
        return event.isCancellable() && ((Cancellable) event).isCancelled();
    }

    private ListenerList getListenerList(Class<?> eventType) {
        ListenerList result = listenerLists.get(eventType);
        if (result == null) {
            result = createListenerList(eventType);
            listenerLists.put(eventType, result);
        }
        return result;
    }

    private ListenerList createListenerList(Class<?> eventType) {
        ListenerList listenerList = new ListenerList();
        for (Map.Entry<Class<?>, ListenerList> entry : listenerLists.entrySet()) {
            if (entry.getKey().isAssignableFrom(eventType)) {
                listenerList.addParent(entry.getValue());
            } else if (eventType.isAssignableFrom(entry.getKey())) {
                listenerList.addChild(entry.getValue());
            }
        }
        return listenerList;
    }

    @Override
    public void register(Object target) {
        if (ownerToListeners.containsKey(target)) {
            throw new IllegalStateException("Listener has been registered");
        }

        boolean isStatic = target.getClass() == Class.class;
        Class<?> targetClass = isStatic ? (Class<?>) target : target.getClass();
        List<ListenerWrapper> listeners = new ArrayList<>();
        for (Method method : targetClass.getDeclaredMethods()) {
            Listener annotation = method.getAnnotation(Listener.class);
            if (annotation != null && Modifier.isStatic(method.getModifiers()) == isStatic) {
                listeners.add(registerListener(target, method, annotation, isStatic));
            }
        }
        if (listeners.isEmpty()) {
            throw new IllegalArgumentException(targetClass + " has no @Listener method");
        }
        ownerToListeners.put(target, listeners.toArray(ListenerWrapper.EMPTY_ARRAY));
    }

    private ListenerWrapper registerListener(Object owner, Method method, Listener listener, boolean isStatic) {
        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException(String.format("The count of listener method parameter must be 1. Listener: %s.%s(?)", method.getDeclaringClass().getName(), method.getName()));
        }

        Class<?> eventType = method.getParameterTypes()[0];
        if (!Event.class.isAssignableFrom(eventType)) {
            throw new IllegalArgumentException(String.format("The type of parameter of listener method must be Event or its sub class. Listener: %s.%s(%s)", method.getDeclaringClass().getName(), method.getName(), eventType.getName()));
        }

        if (method.getReturnType() != void.class) {
            throw new IllegalArgumentException(String.format("The return type of listener method must be void. Listener: %s.%s(%s)", method.getDeclaringClass().getName(), method.getName(), eventType.getName()));
        }

        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException(String.format("Listener method must be public. Listener: %s.%s(%s)", method.getDeclaringClass().getName(), method.getName(), eventType.getName()));
        }

        if (Modifier.isAbstract(method.getModifiers())) {
            throw new IllegalArgumentException(String.format("Listener method cannot be abstract. Listener: %s.%s(%s)", method.getDeclaringClass().getName(), method.getName(), eventType.getName()));
        }

        // Get generic type.
        Type genericType = null;
        if (GenericEvent.class.isAssignableFrom(eventType)) {
            Type type = method.getGenericParameterTypes()[0];
            genericType = type instanceof ParameterizedType ? ((ParameterizedType) type).getActualTypeArguments()[0] : null;
            if (genericType instanceof ParameterizedType) {
                genericType = ((ParameterizedType) genericType).getRawType();
            }
        }

        ListenerInvoker listenerInvoker;
        try {
            listenerInvoker = createInvoker(owner, method, isStatic);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Failed to create listener invoker. Listener: %s.%s(%s)", method.getDeclaringClass().getName(), method.getName(), eventType.getName()));
        }
        ListenerWrapper listenerWrapper = new ListenerWrapper(eventType, genericType, listener.order(), listener.receiveCancelled(), listenerInvoker);
        getListenerList(eventType).register(listenerWrapper);
        return listenerWrapper;
    }

    protected ListenerInvoker createInvoker(Object owner, Method method, boolean isStatic) throws Exception {
        MethodHandle handle = MethodHandles.publicLookup().unreflect(method);
        return new MethodHandleListenerInvoker(isStatic ? handle : handle.bindTo(owner));
    }

    @Override
    public void unregister(Object target) {
        ListenerWrapper[] listeners = ownerToListeners.remove(target);
        if (listeners != null) {
            for (ListenerWrapper listener : listeners) {
                getListenerList(listener.getEventType()).unregister(listener);
            }
        }
    }

    @Override
    public <T extends Event> void addListener(Consumer<T> consumer) {
        addListener(Order.DEFAULT, false, consumer);
    }

    @Override
    public <T extends Event> void addListener(Order order, Consumer<T> consumer) {
        addListener(order, false, consumer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void addListener(Order order, boolean receiveCancelled, Consumer<T> consumer) {
        Class<?> eventType = TypeResolver.resolveRawArgument(Consumer.class, consumer.getClass());
        if (eventType == TypeResolver.Unknown.class) {
            throw new IllegalStateException("Failed to resolve consumer event type");
        }
        addListener(order, receiveCancelled, (Class<T>) eventType, consumer);
    }

    @Override
    public <T extends Event> void addListener(Order order, boolean receiveCancelled, Class<T> eventType, Consumer<T> consumer) {
        if (order == null) throw new NullPointerException("order cannot be null");
        if (eventType == null) throw new NullPointerException("eventType cannot be null");
        if (consumer == null) throw new NullPointerException("consumer cannot be null");
        if (ownerToListeners.containsKey(consumer)) throw new IllegalStateException("Listener has been registered");
        ListenerWrapper listenerWrapper = new ListenerWrapper(eventType, null, order, receiveCancelled, new ConsumerListenerInvoker<>(consumer));
        getListenerList(eventType).register(listenerWrapper);
        ownerToListeners.put(consumer, new ListenerWrapper[]{listenerWrapper});
    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Consumer<T> consumer) {
        addGenericListener(genericType, Order.DEFAULT, false, consumer);
    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, Consumer<T> consumer) {
        addGenericListener(genericType, order, false, consumer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, boolean receiveCancelled, Consumer<T> consumer) {
        Class<?> eventType = TypeResolver.resolveRawArgument(Consumer.class, consumer.getClass());
        if (eventType == TypeResolver.Unknown.class) {
            throw new IllegalStateException("Failed to resolve consumer event type");
        }
        addGenericListener(genericType, order, receiveCancelled, (Class<T>) eventType, consumer);
    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, boolean receiveCancelled, Class<T> eventType, Consumer<T> consumer) {
        if (genericType == null) throw new NullPointerException("genericType cannot be null");
        if (order == null) throw new NullPointerException("order cannot be null");
        if (eventType == null) throw new NullPointerException("eventType cannot be null");
        if (consumer == null) throw new NullPointerException("consumer cannot be null");
        if (ownerToListeners.containsKey(consumer)) throw new IllegalStateException("Listener has been registered");
        ListenerWrapper listenerWrapper = new ListenerWrapper(eventType, genericType, order, receiveCancelled, new ConsumerListenerInvoker<>(consumer));
        getListenerList(eventType).register(listenerWrapper);
        ownerToListeners.put(consumer, new ListenerWrapper[]{listenerWrapper});
    }
}
