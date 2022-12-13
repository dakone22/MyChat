package client;

import core.events.ExceptionOccurredEvent;
import core.events.MessageReceivedEvent;

public interface ClientOutput {
    void newMessage(MessageReceivedEvent event);
    void exceptionOccurred(ExceptionOccurredEvent event);

}
