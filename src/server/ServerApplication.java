package server;

import core.events.ExceptionOccurredEvent;
import core.events.server.ClientJoinEvent;
import core.events.server.ClientLeaveEvent;
import core.listeners.ServerListener;
import core.network.packets.s2c.chat.ClientLeaveS2CPacket;

public class ServerApplication {
    private final ServerOutput output;
    private final Server server;

    private void setupListeners() {
        server.addMessageReceivedListener(e -> {
            output.onNewMessage(e);
            // TODO: echo messages
        });
        server.addExceptionOccurredListener(output::onExceptionOccurred);
        server.addServerListener(new ServerListener() {
            @Override
            public void clientJoined(ClientJoinEvent e) {
                output.onClientJoin(e);
                server.sendUserJoin((ClientHandler) e.client);  // TODO: UserClass
            }

            @Override
            public void clientLeaved(ClientLeaveEvent e) {
                output.onClientLeaved(e);
                server.sendUserLeave((ClientHandler) e.client, ClientLeaveS2CPacket.DisconnectReason.Error);  // TODO: proper disconnect and leave reasons
            }
        });
    }

    public ServerApplication(ServerOutput serverOutput) {
        output = serverOutput;
        server = new Server();

        setupListeners();
    }

    public void start(int port) {
        try {
            server.start(port);
            output.onServerStart();
        } catch (Exception e) {
            output.onExceptionOccurred(new ExceptionOccurredEvent(this, e));
        }
    }

    public void send(String text) {
        server.sendCustomMessage(text);
    }
}
