package core.listenables;

import core.Listenable;
import core.events.MessageReceivedEvent;
import core.listeners.MessageReceivedListener;

public interface MessageReceivedListenable extends Listenable {
    default void addMessageReceivedListener(MessageReceivedListener listener) {
        addListener(MessageReceivedListener.class, listener);
    }

    default void removeMessageReceivedListener(MessageReceivedListener listener) {
        removeListener(MessageReceivedListener.class, listener);
    }

    default void messageReceived(MessageReceivedEvent e) {
        for (var listener : getListeners(MessageReceivedListener.class))
            listener.messageReceived(e);
    }
}
