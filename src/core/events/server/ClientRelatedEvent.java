package core.events.server;

import core.Event;

public abstract class ClientRelatedEvent extends Event {
    public final Object client;

    protected ClientRelatedEvent(Object sender, Object client) {
        super(sender);
        this.client = client;
    }
}
