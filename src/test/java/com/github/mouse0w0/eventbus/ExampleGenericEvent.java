package com.github.mouse0w0.eventbus;

import java.lang.reflect.Type;

public class ExampleGenericEvent<T> extends GenericEvent.Impl<T> {
    public ExampleGenericEvent(Type genericType) {
        super(genericType);
    }
}
