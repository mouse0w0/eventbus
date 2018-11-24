package com.github.mouse0w0.eventbus;

import java.lang.reflect.Type;

public interface GenericEvent<T> extends Event {

    Type getGenericType();

    abstract class Impl<T> implements GenericEvent<T> {

        private final Type genericType;

        public Impl(Type genericType) {
            this.genericType = genericType;
        }

        @Override
        public Type getGenericType() {
            return genericType;
        }
    }
}
