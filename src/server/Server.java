package server;

import core.Observable;
import core.events.ExceptionOccurredEvent;
import core.events.server.ClientJoinEvent;
import core.events.server.ClientLeaveEvent;
import core.listenables.ExceptionOccurredListenable;
import core.listenables.MessageReceivedListenable;
import core.listenables.ServerListenable;
import core.network.packets.s2c.chat.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Server extends Observable implements ExceptionOccurredListenable, MessageReceivedListenable, ServerListenable {
    private ServerSocket serverSocket;

    // region Client Handling

    private final List<ClientHandler> clients = new ArrayList<>();
    private int messageId = 0;

    private void acceptNewClient(Socket clientSocket) throws IOException {
        var client = new ClientHandler(clientSocket);

        client.addMessageReceivedListener(this::messageReceived);
        client.addExceptionOccurredListener(e -> {
            clients.remove(client);
            clientLeaved(new ClientLeaveEvent(this, client));
            exceptionOccurred(e);  // TODO: print client's exception?
        });

        clients.add(client);

        clientJoined(new ClientJoinEvent(this, client));
    }

    private void send(ClientHandler client, MessageS2CPacket messagePacket) {
        try {
            client.send(messagePacket);
        } catch (InterruptedException e) {
            exceptionOccurred(new ExceptionOccurredEvent(client, e));
        }
    }

    private void send(List<ClientHandler> clients, MessageS2CPacket messagePacket) {
        for (var client : clients)
            send(client, messagePacket);
    }

    private void sendAll(MessageS2CPacket messagePacket) {
        send(this.clients, messagePacket);
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

    public void sendPublicMessage(ClientHandler fromUser, String message) {  // TODO: UserClass
        var messagePacket = new ChatMessageS2CPacket.Public(messageId++, LocalDateTime.now(), fromUser.toString(), message);

        for (ClientHandler client : clients) {
            send(client, messagePacket);
        }
    }

    public void sendPrivateMessage(ClientHandler fromUser, ClientHandler toUser, String message) {  // TODO: UserClass
        var messagePacket = new ChatMessageS2CPacket.Public(messageId++, LocalDateTime.now(), fromUser.toString(), message);
        send(toUser, messagePacket);
    }

    public void sendUserJoin(ClientHandler user) {
        var messagePacket = new ClientJoinS2CPacket(messageId++, LocalDateTime.now(), user.toString());
        sendAll(messagePacket);
    }

    public void sendUserLeave(ClientHandler user, ClientLeaveS2CPacket.DisconnectReason reason) {
        var messagePacket = new ClientLeaveS2CPacket(messageId++, LocalDateTime.now(), user.toString(), reason);
        sendAll(messagePacket);
    }

    @Deprecated
    public void sendCustomMessage(String message) {
        var messagePacket = new CustomSystemMessageS2CPacket(messageId++, LocalDateTime.now(), message);
        sendAll(messagePacket);
    }

    // endregion Public Interface
}
