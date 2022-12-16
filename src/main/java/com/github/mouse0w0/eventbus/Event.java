package com.github.mouse0w0.eventbus;

/**
 * Every event class should implement this interface.
 */
public interface Event {
	default boolean isCancellable() {
        return this instanceof Cancellable;
	}
}
