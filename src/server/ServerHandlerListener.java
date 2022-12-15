package server;

import core.ExceptionOccurredListener;

public interface ServerHandlerListener extends ExceptionOccurredListener {
    void onServerStart();

    void onClientJoin(ClientConnectionHandler client);

    void onClientLeave(ClientConnectionHandler client);

    void onClientDisconnect(ClientConnectionHandler client);

    void onPublicMessage(ClientConnectionHandler sender, String message);

    void onPrivateMessage(ClientConnectionHandler sender, String receiver, String message);
}
