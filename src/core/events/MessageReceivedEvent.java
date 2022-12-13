package core.events;

import core.Event;

public class MessageReceivedEvent extends Event {

    public final String message;

    public MessageReceivedEvent(Object sender, String message) {
        super(sender);
        this.message = message;
    }
}
