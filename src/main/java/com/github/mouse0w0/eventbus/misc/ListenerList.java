package com.github.mouse0w0.eventbus.misc;

import com.github.mouse0w0.eventbus.Event;
import com.github.mouse0w0.eventbus.Order;

import java.util.*;

public class ListenerList {

    private final Class<?> eventType;
    private final List<ListenerList> parentListenerLists = new ArrayList<>();

    private final EnumMap<Order, List<RegisteredListener>> listeners = new EnumMap<>(Order.class);

    public ListenerList(Class<?> eventType) {
        this.eventType = eventType;
    }

    public void post(Event event) throws Exception {
        for (Order order : Order.values()) {
            for (RegisteredListener listener : listeners.getOrDefault(order, Collections.emptyList())) {
                listener.post(event);
            }
            for (ListenerList parent : parentListenerLists) {
                for (RegisteredListener listener : parent.listeners.getOrDefault(order, Collections.emptyList())) {
                    listener.post(event);
                }
            }
        }
    }

    public void register(RegisteredListener listener) {
        listeners.computeIfAbsent(listener.getOrder(), order -> new ArrayList<>()).add(listener);
    }

    public void unregister(RegisteredListener listener) {
        listeners.computeIfAbsent(listener.getOrder(), order -> new ArrayList<>()).remove(listener);
    }

    public void addParent(ListenerList list) {
        parentListenerLists.add(list);
    }
}
