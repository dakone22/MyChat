package core;

abstract public class Event {
    public final Object sender;

    protected Event(Object sender) {
        this.sender = sender;
    }
}
