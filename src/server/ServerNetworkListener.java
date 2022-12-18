package server;

import core.ExceptionOccurredListener;
import core.User;

public interface ServerNetworkListener extends ExceptionOccurredListener {
    void onServerStart();

    void onClientExceptionDisconnected(ClientConnectionHandler client, Throwable exception);

    void onPublicMessage(ClientConnectionHandler sender, String message);

    void onPrivateMessage(ClientConnectionHandler sender, User user, String message);

    void onLogin(ClientConnectionHandler client, User requestedUserData, String hashedPassword);

    void onDisconnect(ClientConnectionHandler client);

    void onUserListRequest(ClientConnectionHandler client);

    void onNewClient(ClientConnectionHandler client);

    void onServerStop();
}
