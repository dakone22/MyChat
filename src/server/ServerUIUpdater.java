package server;

import core.ExceptionOccurredListener;
import core.User;

public interface ServerUIUpdater extends ExceptionOccurredListener {
    void onServerStart();

    void onServerStop();

    void onPublicMessage(User sender, String message);

    void onPrivateMessage(User sender, User receiver, String message);

    void onClientJoined(User newUser);

    void onClientLeft(User user);

    void onClientExceptionDisconnected(User client, Throwable exception);

    void updateUserList(Iterable<User> users);

    void onAuthorizationTimeout(ClientConnectionHandler client);

    void onAuthorizationFailed(ClientConnectionHandler client);

    void onCustomMessage(String msg);
}
