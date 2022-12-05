package server;

public interface ServerObserver {

    void onMessageReceived(ClientHandler client, String message);

    void onClientJoin(ClientHandler client);

    void onClientDisconnected(ClientHandler client, Throwable exception);
}
