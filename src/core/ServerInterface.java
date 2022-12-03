package core;

import core.events.MessageSendEvent;

import java.io.IOException;

public class ServerInterface {
    private Server server;

    public ServerInterface() {
        server = new Server();

        server.addMessageSendListener(e -> {
            if (e.type == MessageSendEvent.Type.Private) {
                System.out.printf("[SERVER to %s]: %s", ((MessageSendEvent.Private) e).getDestination(), e.getMessage());
            } else {
                System.out.printf("[SERVER]: %s", e.getMessage());
            }
        });

        try {
            server.start(6957);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
