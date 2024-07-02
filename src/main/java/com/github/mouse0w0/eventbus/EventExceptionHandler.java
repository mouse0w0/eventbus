package com.github.mouse0w0.eventbus;

public interface EventExceptionHandler {
    EventExceptionHandler IGNORE = new EventExceptionHandler() {
        @Override
        public void handle(Event event, Throwable throwable) {
            // Nothing to do.
        }
    };
    EventExceptionHandler RETHROW = new EventExceptionHandler() {
        @Override
        public void handle(Event event, Throwable throwable) {
            rethrow(throwable);
        }

        @SuppressWarnings("unchecked")
        private <T extends Throwable> void rethrow(Throwable t) throws T {
            throw (T) t;
        }
    };
    EventExceptionHandler PRINT = new EventExceptionHandler() {
        @Override
        public void handle(Event event, Throwable throwable) {
            throwable.printStackTrace();
        }
    };

    void handle(Event event, Throwable throwable);
}
