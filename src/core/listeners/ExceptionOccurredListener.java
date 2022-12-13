package core.listeners;

import core.events.ExceptionOccurredEvent;

import java.util.EventListener;

public interface ExceptionOccurredListener extends EventListener {
    void exceptionOccurred(ExceptionOccurredEvent e);
}
