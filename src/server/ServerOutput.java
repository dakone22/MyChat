package server;

import core.events.server.ClientJoinEvent;
import core.events.ExceptionOccurredEvent;
import core.events.MessageReceivedEvent;
import core.events.server.ClientLeaveEvent;

public interface ServerOutput {
    void onNewMessage(MessageReceivedEvent event);
    void onExceptionOccurred(ExceptionOccurredEvent event);
    void onClientJoin(ClientJoinEvent event);
    void onClientLeaved(ClientLeaveEvent event);

    void onServerStart();
}
