package core.listeners;

import core.events.server.ClientJoinEvent;
import core.events.server.ClientLeaveEvent;

import java.util.EventListener;

public interface ServerListener extends EventListener {
    void clientJoined(ClientJoinEvent e);
    void clientLeaved(ClientLeaveEvent e);
}
