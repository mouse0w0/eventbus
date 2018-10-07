package com.github.mouse0w0.eventbus;

public class ExampleListener {

    private boolean parent = false;

    @Listener(receiveCancelled = true)
    public void onDefualt(ExampleEvent event) {
        assert event.getCurrentState() == Order.DEFAULT;
        event.setCurrentState(Order.LATE);
    }

    @Listener(order = Order.EARLY)
    public void onEarly(ExampleEvent event) {
        assert event.getCurrentState() == Order.EARLY;
        event.setCurrentState(Order.DEFAULT);
        event.setCancelled(true);
    }

    @Listener(order = Order.LAST)
    public void onLast(ExampleEvent event) {
        assert event.getCurrentState() == Order.LAST;
        assert parent;
        event.setCancelled(true);
    }

    @Listener(order = Order.LATE)
    public void onLate(ExampleEvent event) {
        assert event.getCurrentState() == Order.LATE;
        event.setCancelled(false);
        event.setCurrentState(Order.LAST);
    }

    @Listener(order = Order.FIRST)
    public void onFirst(ExampleEvent event) {
        assert event.getCurrentState() == Order.FIRST;
        event.setCurrentState(Order.EARLY);
    }

    @Listener
    public void onCancelled(ExampleEvent event) {
        assert false;
    }

    @Listener(order = Order.FIRST)
    public void onParent(Event event) {
        assert event.getClass() == ExampleEvent.class;
        parent = true;
    }
}
