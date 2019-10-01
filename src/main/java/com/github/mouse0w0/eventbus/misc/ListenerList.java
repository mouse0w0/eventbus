package com.github.mouse0w0.eventbus.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ListenerList {

    private final Class<?> eventType;
    private final List<ListenerList> children = new ArrayList<>();

    private final Collection<RegisteredListener> listeners = SortedList.create(Comparator.comparingInt(o -> o.getOrder().ordinal()), ArrayList::new);

    public ListenerList(Class<?> eventType) {
        this.eventType = eventType;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void register(RegisteredListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
        children.forEach(listenerList -> {
            synchronized (listenerList.listeners) {
                listenerList.listeners.add(listener);
            }
        });
    }

    public void unregister(RegisteredListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
        children.forEach(listenerList -> {
            synchronized (listenerList.listeners) {
                listenerList.listeners.remove(listener);
            }
        });
    }

    public void addParent(ListenerList parent) {
        parent.children.add(this);
        synchronized (listeners) {
            listeners.addAll(parent.listeners);
        }
    }

    public void addChild(ListenerList child) {
        children.add(child);
        synchronized (child.listeners) {
            child.listeners.addAll(listeners);
        }
    }

    public Collection<RegisteredListener> getListeners() {
        return listeners;
    }
}
