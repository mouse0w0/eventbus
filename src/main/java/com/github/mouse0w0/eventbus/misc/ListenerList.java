package com.github.mouse0w0.eventbus.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ListenerList implements Iterable<RegisteredListener> {
    private final List<ListenerList> children = new ArrayList<>();
    private final List<RegisteredListener> listeners = new ArrayList<>();

    public void register(RegisteredListener listener) {
        addListener(listener);
        for (ListenerList child : children) {
            child.addListener(listener);
        }
    }

    public void unregister(RegisteredListener listener) {
        removeListener(listener);
        for (ListenerList child : children) {
            child.removeListener(listener);
        }
    }

    private void addListener(RegisteredListener listener) {
        for (int i = 0, size = listeners.size(); i < size; i++) {
            if (listener.getOrder().compareTo(listeners.get(i).getOrder()) < 0) {
                listeners.add(i, listener);
                return;
            }
        }
        listeners.add(listener);
    }

    private void removeListener(RegisteredListener listener) {
        listeners.remove(listener);
    }

    public void addParent(ListenerList parent) {
        parent.children.add(this);
        listeners.addAll(parent.listeners);
    }

    public void addChild(ListenerList child) {
        children.add(child);
        child.listeners.addAll(listeners);
    }

    @Override
    public Iterator<RegisteredListener> iterator() {
        return listeners.iterator();
    }
}
