package com.github.mouse0w0.eventbus;

public class ExampleGenericListener {

    public boolean genericTestDone;
    public boolean normalTestDone;

    @Listener
    public void onGeneric$1(ExampleGenericEvent<String> event) {
        genericTestDone = true;
    }

    @Listener
    public void onGeneric$2(ExampleGenericEvent<Object> event) {
        assert false;
    }

    @Listener
    public void onEvent(ExampleGenericEvent event) {
        normalTestDone = true;
    }
}
