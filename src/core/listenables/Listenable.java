package core.listenables;

import java.util.EventListener;

public interface Listenable {
    <T extends EventListener> void addListener(Class<T> listenerType, T listener);
    <T extends EventListener> void removeListener(Class<T> listenerType, T listener);
    <T extends EventListener> T[] getListeners(Class<T> listenerType);
//    private final Map<Class<? extends Event>, List<EventListener>> listeners = new HashMap<>();
//
//    protected void addListener(Class<? extends Event> eventType, EventListener listener) {
//        if (!listeners.containsKey(eventType))
//            listeners.put(eventType, new ArrayList<>());
//
//        listeners.get(eventType).add(listener);
//    }
//
//    protected void removeListener(Class<? extends Event> eventType, EventListener listener) {
//        if (!listeners.containsKey(eventType)) return;
//
//        var listenerList = listeners.get(eventType);
//        listenerList.remove(listener);
//
//        if (listenerList.isEmpty())
//            listeners.remove(eventType);
//    }

//    public void notifyListeners(Event event) {
//        for (var listener : listeners.getOrDefault(event.getClass(), Collections.emptyList()))
//            listener.notify(event);
//    }
}
