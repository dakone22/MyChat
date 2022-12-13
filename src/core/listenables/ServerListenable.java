package core.listenables;

import core.events.server.ClientJoinEvent;
import core.events.server.ClientLeaveEvent;
import core.listeners.ServerListener;

public interface ServerListenable extends Listenable {

    default void addServerListener(ServerListener listener) {
        addListener(ServerListener.class, listener);
    }

    default void removeServerListener(ServerListener listener) {
        removeListener(ServerListener.class, listener);
    }

    default void clientJoined(ClientJoinEvent e) {
        for (var listener : getListeners(ServerListener.class))
            listener.clientJoined(e);
    }

    default void clientLeaved(ClientLeaveEvent e) {
        for (var listener : getListeners(ServerListener.class))
            listener.clientLeaved(e);
    }
}
