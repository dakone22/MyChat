package core.events;

import core.Event;

public class ExceptionOccurredEvent extends Event {
    public final Throwable exception;

    public ExceptionOccurredEvent(Object sender, Throwable exception) {
        super(sender);
        this.exception = exception;
    }
}
