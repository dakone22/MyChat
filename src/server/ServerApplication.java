package server;

import core.events.ExceptionOccurredEvent;
import core.events.server.ClientJoinEvent;
import core.events.server.ClientLeaveEvent;
import core.listeners.ServerListener;

public class ServerApplication {
    private final ServerOutput output;
    private final Server server;

    private void setupListeners() {
        server.addMessageReceivedListener(output::onNewMessage);
        server.addExceptionOccurredListener(output::onExceptionOccurred);
        server.addServerListener(new ServerListener() {
            @Override
            public void clientJoined(ClientJoinEvent e) {
                output.onClientJoin(e);
                server.sendPublicMessage("HELLO NEW CLIENT");
            }

            @Override
            public void clientLeaved(ClientLeaveEvent e) {
                output.onClientLeaved(e);
                server.sendPublicMessage("BYE CLIENT");

                // TODO: where to SendPublicMessage?
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
        server.sendPublicMessage(text);
    }
}
