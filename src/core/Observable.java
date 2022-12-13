package core;

import core.listenables.Listenable;

import javax.swing.event.EventListenerList;
import java.util.EventListener;

public abstract class Observable implements Listenable {
    protected final EventListenerList listenerList = new EventListenerList();

    @Override
    public <T extends EventListener> void addListener(Class<T> listenerType, T listener) {
        listenerList.add(listenerType, listener);
    }

    @Override
    public <T extends EventListener> void removeListener(Class<T> listenerType, T listener) {
        listenerList.remove(listenerType, listener);
    }

    @Override
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerList.getListeners(listenerType);
    }
}
