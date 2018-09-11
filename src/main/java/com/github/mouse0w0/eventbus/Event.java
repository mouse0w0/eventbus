package com.github.mouse0w0.eventbus;

public interface Event {

	default boolean isCancellable() {
		return this instanceof Cancellable;
	}
}
