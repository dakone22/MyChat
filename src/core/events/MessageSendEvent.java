package core.events;


import server.ClientHandler;

import java.util.EventObject;

public class MessageSendEvent extends EventObject {
    private final String message;

    public enum Type { Public, Private }

    public final Type type;

    private MessageSendEvent(Object source, String message, Type type) {
        super(source);
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public static class Public extends MessageSendEvent {
        public Public(Object source, String message) {
            super(source, message, Type.Public);
        }
    }

    public static class Private extends MessageSendEvent {
        private final Object destination;
        public Private(Object source, Object destination, String message) {
            super(source, message, Type.Private);
            this.destination = destination;
        }

        public Object getDestination() {
            return destination;
        }
    }
}