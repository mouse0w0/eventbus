package com.github.mouse0w0.eventbus.misc;

import java.util.*;
import java.util.function.Supplier;

final class SortedList<E> extends AbstractList<E> {

    public static <E> SortedList<E> create(Comparator<E> comparator, Supplier<List<E>> constructor) {
        return new SortedList<>(constructor.get(), comparator);
    }

    private final List<E> list;
    private final Comparator<E> comparator;

    private SortedList(List<E> list, Comparator<E> comparator) {
        this.list = list;
        this.comparator = comparator;
    }

    @Override
    public boolean add(E e) {
        Objects.requireNonNull(e, "Element cannot be null.");
        int index = 0;
        for (int size = size(); index < size; index++) {
            if (comparator.compare(e, get(index)) < 0) {
                list.add(index, e);
                return true;
            }
        }
        return list.add(e);
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E remove(int index) {
        return list.remove(index);
    }

    @Override
    public int size() {
        return list.size();
    }
}
