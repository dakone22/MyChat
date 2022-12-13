package core.listenables;

import core.events.ExceptionOccurredEvent;
import core.listeners.ExceptionOccurredListener;

public interface ExceptionOccurredListenable extends Listenable {
    default void addExceptionOccurredListener(ExceptionOccurredListener listener) {
        addListener(ExceptionOccurredListener.class, listener);
    }

    default void removeExceptionOccurredListener(ExceptionOccurredListener listener) {
        removeListener(ExceptionOccurredListener.class, listener);
    }

    default void exceptionOccurred(ExceptionOccurredEvent e) {
        for (var listener : getListeners(ExceptionOccurredListener.class))
            listener.exceptionOccurred(e);
    }
}
