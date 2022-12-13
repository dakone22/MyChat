package core.events.server;

public class ClientLeaveEvent extends ClientRelatedEvent {
    protected ClientLeaveEvent(Object sender, Object client) {
        super(sender, client);
    }
}
