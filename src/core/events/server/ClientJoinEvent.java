package core.events.server;

public class ClientJoinEvent extends ClientRelatedEvent {
    public ClientJoinEvent(Object sender, Object client) {
        super(sender, client);
    }
}
