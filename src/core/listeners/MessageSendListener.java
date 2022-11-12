package core.listeners;

import core.events.MessageSendEvent;

import java.util.EventListener;

public interface MessageSendListener extends EventListener {
    void onMessageSend(MessageSendEvent e);
}
