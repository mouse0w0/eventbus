package com.github.mouse0w0.eventbus;

public interface WrappedListener {

    void post(Event event) throws Exception;
}
