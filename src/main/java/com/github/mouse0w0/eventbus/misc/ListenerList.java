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
        int left = 0, right = listeners.size();
        while (left < right) {
            int mid = (left + right) >>> 1;
            if (compareListener(listener, listeners.get(mid)) < 0) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        listeners.add(left, listener);
    }

    private void removeListener(RegisteredListener listener) {
        listeners.remove(listener);
    }

    private int compareListener(RegisteredListener o1, RegisteredListener o2) {
        return o1.getOrder().compareTo(o2.getOrder());
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
