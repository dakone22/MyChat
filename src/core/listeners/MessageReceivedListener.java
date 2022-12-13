package core.listeners;

import core.events.MessageReceivedEvent;

import java.util.EventListener;

public interface MessageReceivedListener extends EventListener {
    void messageReceived(MessageReceivedEvent e);
}
