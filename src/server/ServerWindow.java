package server;

import java.io.IOException;

public class ServerWindow {

    private Server server;

    public ServerWindow() {
        server = new Server(new ServerObserver() {
            @Override
            public void onMessageReceived(ClientHandler client, String message) {
                String response = "%s: %s".formatted(client.toString(), message);

                System.out.println(response);
                server.sendPublicMessage(response);
            }

            @Override
            public void onClientJoin(ClientHandler client) {
                System.out.printf("New client %s\n", client.toString());
                server.sendPublicMessage("Hello, new client %s!".formatted(client.toString()));
            }

            @Override
            public void onClientDisconnected(ClientHandler client, Throwable exception) {
                System.out.printf("Client %s disconnected: %s\n", client.toString(), exception.toString());
                server.sendPublicMessage("Bye, client %s.".formatted(client.toString()));
            }

        });

        try {
            server.start(6957);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
