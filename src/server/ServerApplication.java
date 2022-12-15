package server;

import core.User;
import core.network.packets.s2c.chat.ClientLeaveS2CPacket;

import java.util.List;

public class ServerApplication {
    private final ServerApplicationListener output;
    private final ServerHandler serverHandler;

    private final List<User> userList;

    public ServerApplication(ServerApplicationListener serverApplicationListener) {
        output = serverApplicationListener;
        serverHandler = new ServerHandler(getHandlerListener());
    }

    private ServerHandlerListener getHandlerListener() {
        return new ServerHandlerListener() {
            @Override
            public void onServerStart() {
                output.onServerStart();
            }

            @Override
            public void onClientJoin(ClientConnectionHandler client) {
                output.onClientJoin(client);
                serverHandler.sendUserJoin(client);
            }

            @Override
            public void onClientLeave(ClientConnectionHandler client) {
                output.onClientLeave(client);
                serverHandler.sendUserLeave(client, ClientLeaveS2CPacket.DisconnectReason.SelfDisconnect);
            }

            @Override
            public void onClientDisconnect(ClientConnectionHandler client) {
                output.onClientLeave(client);
                serverHandler.sendUserLeave(client, ClientLeaveS2CPacket.DisconnectReason.Error);
            }

            @Override
            public void onPublicMessage(ClientConnectionHandler sender, String message) {
                output.onPublicMessage(sender, message);
                serverHandler.sendPublicMessage(sender, message);
            }

            @Override
            public void onPrivateMessage(ClientConnectionHandler sender, String receiver, String message) {
                output.onPrivateMessage(sender, receiver, message);
                serverHandler.sendPublicMessage(sender, message);
            }

            @Override
            public void exceptionOccurred(Object sender, Throwable exception) {
                output.exceptionOccurred(sender, exception);

                if (sender == serverHandler)
                    stop();
            }
        };
    }

    public void start(int port) {
        try {
            serverHandler.start(port);
        } catch (Exception e) {
            output.exceptionOccurred(this, e);
        }
    }

    public void stop() {
        try {
            serverHandler.stop();
        } catch (Exception e) {
            output.exceptionOccurred(this, e);
        }
    }

    public void send(String text) {
        serverHandler.sendCustomMessage(text);
    }
}
