package server;

public interface ClientHandlerObserver {
    void onMessageReceived(ClientHandler client, String message);

    void onExceptionOccurred(ClientHandler client, Throwable exception);
}
