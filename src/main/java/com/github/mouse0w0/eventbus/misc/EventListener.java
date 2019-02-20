package com.github.mouse0w0.eventbus.misc;

import com.github.mouse0w0.eventbus.Event;

public interface EventListener {

    void post(Event event) throws Exception;
}
