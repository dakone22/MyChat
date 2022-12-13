package server;

import core.Observable;
import core.events.server.ClientJoinEvent;
import core.events.ExceptionOccurredEvent;
import core.listenables.ExceptionOccurredListenable;
import core.listenables.MessageReceivedListenable;
import core.listenables.ServerListenable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Observable implements ExceptionOccurredListenable, MessageReceivedListenable, ServerListenable {
    private ServerSocket serverSocket;

    // region Client Handling

    private final List<ClientHandler> clients = new ArrayList<>();

    private void acceptNewClient(Socket clientSocket) throws IOException {
        var client = new ClientHandler(clientSocket);

        client.addMessageReceivedListener(this::messageReceived);
        client.addExceptionOccurredListener(e -> {
            clients.remove(client);
            exceptionOccurred(e);
        });

        clients.add(client);

        clientJoined(new ClientJoinEvent(this, client));
    }

    private void sendMessage(ClientHandler client, String message) {
        try {
            client.send(message);
        } catch (InterruptedException e) {
            exceptionOccurred(new ExceptionOccurredEvent(client, e));
        }
    }

    // endregion Client Handling


    // region Public Interface

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        var acceptionThread = new Thread(() -> {
            try {
                while (!serverSocket.isClosed()) {
                    var clientSocket = serverSocket.accept();

                    acceptNewClient(clientSocket);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        acceptionThread.setUncaughtExceptionHandler((t, e) -> exceptionOccurred(new ExceptionOccurredEvent(t, e)));
        acceptionThread.start();
    }

    public void stop() throws IOException {
        if (serverSocket == null) return;
        if (serverSocket.isClosed()) return;

        serverSocket.close();

        for (ClientHandler c : clients)
            c.close();

        clients.clear();
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
