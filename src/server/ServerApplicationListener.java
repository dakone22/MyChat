package server;

import core.ExceptionOccurredListener;

public interface ServerApplicationListener extends ExceptionOccurredListener {
    void onServerStart();

    void onClientJoin(ClientConnectionHandler client);

    void onClientLeave(ClientConnectionHandler client);

    void onPublicMessage(ClientConnectionHandler sender, String message);

    void onPrivateMessage(ClientConnectionHandler sender, String receiver, String message);
}
