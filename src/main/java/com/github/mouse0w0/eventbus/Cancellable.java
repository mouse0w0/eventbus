package com.github.mouse0w0.eventbus;

public interface Cancellable {
	
	boolean isCancelled();
	
	void setCancelled(boolean cancelled);
}
