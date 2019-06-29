package com.github.mouse0w0.eventbus.misc;

import java.util.*;

public class ListenerList {

    private final Class<?> eventType;
    private final List<ListenerList> children = new ArrayList<>();

    private final Queue<RegisteredListener> listeners = new PriorityQueue<>(Comparator.comparing(RegisteredListener::getOrder));

    public ListenerList(Class<?> eventType) {
        this.eventType = eventType;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void register(RegisteredListener listener) {
        listeners.add(listener);
        children.forEach(listenerList -> listenerList.listeners.add(listener));
    }

    public void unregister(RegisteredListener listener) {
        listeners.remove(listener);
        children.forEach(listenerList -> listenerList.listeners.remove(listener));
    }

    public void addParent(ListenerList parent) {
        parent.children.add(this);
        listeners.addAll(parent.listeners);
    }

    public void addChild(ListenerList child) {
        children.add(child);
        child.listeners.addAll(listeners);
    }

    public Queue<RegisteredListener> getListeners() {
        return listeners;
    }
}
