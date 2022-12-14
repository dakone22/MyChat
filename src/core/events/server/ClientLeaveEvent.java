package core.events.server;

public class ClientLeaveEvent extends ClientRelatedEvent {
    public ClientLeaveEvent(Object sender, Object client) {
        super(sender, client);
    }
}
