package core.events;


import java.util.EventObject;

public class MessageSendEvent extends EventObject {
    private final String message;

    public MessageSendEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}