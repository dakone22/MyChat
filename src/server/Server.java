package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;

    private final ServerObserver observer;

    public Server(ServerObserver observer) {
        this.observer = observer;
    }


    // region Client Handling

    private final ArrayList<ClientHandler> clients = new ArrayList<>();

    private final ClientHandlerObserver clientObserver = new ClientHandlerObserver() {
        @Override
        public void onMessageReceived(ClientHandler client, String message) {
            observer.onMessageReceived(client, message);
        }

        @Override
        public void onExceptionOccurred(ClientHandler client, Throwable exception) {
            clients.remove(client);
            observer.onClientDisconnected(client, exception);
        }
    };

    private void acceptNewClient(Socket clientSocket) {
        try {
            var client = new ClientHandler(clientObserver, clientSocket);
            clients.add(client);

            observer.onClientJoin(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(ClientHandler client, String message) {
        client.send(message);
    }

    // endregion Clients


    // region Public Interface

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        while (!serverSocket.isClosed()) {
            var clientSocket = serverSocket.accept();

            acceptNewClient(clientSocket);
        }
    }

    public void stop() throws IOException {
        if (serverSocket == null) return;
        if (serverSocket.isClosed()) return;

        serverSocket.close();

        for (ClientHandler c : clients)
            c.stop();
    }

    public void sendPublicMessage(String message) {
        for (ClientHandler client : clients) {
            sendMessage(client, message);
        }
    }

    public void sendPrivateMessage(ClientHandler client, String message) {
        sendMessage(client, message);
    }

    // endregion Public Interface
}
