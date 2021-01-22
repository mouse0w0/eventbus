package com.github.mouse0w0.eventbus;

import org.junit.jupiter.api.Assertions;

public class ExampleListener {

    private boolean parent = false;

    @Listener(receiveCancelled = true)
    public void onDefault(ExampleEvent event) {
        Assertions.assertSame(event.getCurrentState(), Order.DEFAULT);
        event.setCurrentState(Order.LATE);
    }

    @Listener(order = Order.EARLY)
    public void onEarly(ExampleEvent event) {
        Assertions.assertSame(event.getCurrentState(), Order.EARLY);
        event.setCurrentState(Order.DEFAULT);
        event.setCancelled(true);
    }

    @Listener(order = Order.LAST)
    public void onLast(ExampleEvent event) {
        Assertions.assertSame(event.getCurrentState(), Order.LAST);
        Assertions.assertTrue(parent);
        event.setCancelled(true);
    }

    @Listener(order = Order.LATE)
    public void onLate(ExampleEvent event) {
        Assertions.assertSame(event.getCurrentState(), Order.LATE);
        event.setCancelled(false);
        event.setCurrentState(Order.LAST);
    }

    @Listener(order = Order.FIRST)
    public void onFirst(ExampleEvent event) {
        Assertions.assertSame(event.getCurrentState(), Order.FIRST);
        event.setCurrentState(Order.EARLY);
    }

    @Listener
    public void onCancelled(ExampleEvent event) {
        Assertions.fail();
    }

    @Listener(order = Order.FIRST)
    public void onParent(Event event) {
        Assertions.assertSame(event.getClass(), ExampleEvent.class);
        parent = true;
    }
}
