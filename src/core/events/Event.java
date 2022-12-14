package core.events;

abstract public class Event {
    public final Object sender;

    /**
     * @param sender Object that is created this event
     */
    protected Event(Object sender) {
        this.sender = sender;
    }
}
