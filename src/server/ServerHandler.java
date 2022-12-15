package server;

import core.network.listeners.ServerPacketListener;
import core.network.packets.c2s.ChatMessageC2SPacket;
import core.network.packets.s2c.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler {
    private ServerSocket serverSocket;
    private final ServerHandlerListener listener;

    // region ClientConnectionHandler Handling

    private final List<ClientConnectionHandler> clients = new ArrayList<>();
    private int messageId = 0;

    public ServerHandler(ServerHandlerListener listener) {
        this.listener = listener;
    }

    private void acceptNewClient(Socket clientSocket) throws IOException {
        var client = new ClientConnectionHandler(clientSocket);

        client.addExceptionOccurredListener((sender, exception) -> {
            clients.remove(client);
            listener.onClientDisconnect(client);
            listener.onExceptionOccurred(sender, exception);
        });

        client.start(new ServerPacketListener() {
            @Override
            public void onPublicMessage(ChatMessageC2SPacket.Public packet) {
                listener.onPublicMessage(client, packet.getMessage());
            }

            @Override
            public void onPrivateMessage(ChatMessageC2SPacket.Private packet) {
                listener.onPrivateMessage(client, packet.getReceiver(), packet.getMessage());
            }
        });

        clients.add(client);

        listener.onClientJoin(client);
    }

    private void send(ClientConnectionHandler client, MessageS2CPacket messagePacket) {
        try {
            client.sendPacket(messagePacket);
        } catch (InterruptedException e) {
            listener.onExceptionOccurred(client, e);
        }
    }

    private void send(List<ClientConnectionHandler> clients, MessageS2CPacket messagePacket) {
        for (var client : clients)
            send(client, messagePacket);
    }

    private void sendAll(MessageS2CPacket messagePacket) {
        send(this.clients, messagePacket);
    }

    // endregion ClientConnectionHandler Handling


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

        acceptionThread.setUncaughtExceptionHandler(listener::onExceptionOccurred);
        acceptionThread.start();

        listener.onServerStart();
    }

    public void stop() throws IOException {
        if (serverSocket == null) return;
        if (serverSocket.isClosed()) return;

        serverSocket.close();

        for (var c : clients)
            c.stop();

        clients.clear();
    }

    public void sendPublicMessage(ClientConnectionHandler fromUser, String message) {  // TODO: UserClass
        var messagePacket = new ChatMessageS2CPacket.Public(messageId++, LocalDateTime.now(), fromUser.toString(), message);

        for (var client : clients) {
            send(client, messagePacket);
        }
    }

    public void sendPrivateMessage(ClientConnectionHandler fromUser, ClientConnectionHandler toUser, String message) {  // TODO: UserClass
        var messagePacket = new ChatMessageS2CPacket.Public(messageId++, LocalDateTime.now(), fromUser.toString(), message);
        send(toUser, messagePacket);
    }

    public void sendUserJoin(ClientConnectionHandler user) {
        var messagePacket = new ClientJoinS2CPacket(messageId++, LocalDateTime.now(), user.toString());
        sendAll(messagePacket);
    }

    public void sendUserLeave(ClientConnectionHandler user, ClientLeaveS2CPacket.DisconnectReason reason) {
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
