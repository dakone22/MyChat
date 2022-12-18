package server;

import core.network.listeners.ClientPacketListener;
import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;
import core.network.packets.c2s.chat.ChatUserListC2SPacket;
import core.network.packets.c2s.chat.PrivateChatMessageC2SPacket;
import core.network.packets.c2s.chat.PublicChatMessageC2SPacket;
import core.network.packets.c2s.service.DisconnectC2SPacket;
import core.network.packets.c2s.service.LoginC2SPacket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerNetwork {
    private ServerSocket serverSocket;

    private final ServerNetworkListener listener;

    private final List<ClientConnectionHandler> clients = new ArrayList<>();

    public ServerNetwork(ServerNetworkListener listener) {
        this.listener = listener;
    }

    private void acceptNewClient(Socket clientSocket) throws IOException {
        var client = new ClientConnectionHandler(clientSocket);

        client.addExceptionOccurredListener((sender, exception) -> {
            clients.remove(client);
            listener.onClientExceptionDisconnected(client, exception);

//            listener.exceptionOccurred(sender, exception);
        });

        client.start(new ServerPacketListener() {
            @Override
            public void onPublicMessage(PublicChatMessageC2SPacket packet) {
                listener.onPublicMessage(client, packet.message());
            }

            @Override
            public void onPrivateMessage(PrivateChatMessageC2SPacket packet) {
                listener.onPrivateMessage(client, packet.receiver(), packet.message());
            }

            @Override
            public void onLogin(LoginC2SPacket packet) {
                listener.onLogin(client, packet.requestedUserData(), packet.hashedPassword());
            }

            @Override
            public void onDisconnect(DisconnectC2SPacket packet) {
                listener.onDisconnect(client);
            }

            @Override
            public void onUserListRequest(ChatUserListC2SPacket packet) {
                listener.onUserListRequest(client);
            }
        });

        clients.add(client);

        listener.onNewClient(client);
    }

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
        acceptionThread.setUncaughtExceptionHandler(listener::exceptionOccurred);

        acceptionThread.start();

        listener.onServerStart();
    }

    public void stop() throws IOException {
        if (serverSocket == null || serverSocket.isClosed()) return;

        for (var c : clients) {
            c.stop();
        }

        serverSocket.close();
        clients.clear();

        listener.onServerStop();
    }

    public void send(ClientConnectionHandler client, Packet<? extends ClientPacketListener> packet) {
        try {
            client.sendPacket(packet);
        } catch (InterruptedException e) {
            listener.exceptionOccurred(client, e);
        }
    }

    public void send(Iterable<ClientConnectionHandler> clients, Packet<? extends ClientPacketListener> packet) {
        for (var client : clients) {
            send(client, packet);
        }
    }

//    public void sendAllExcept(ClientConnectionHandler clientException, Packet<? extends ClientPacketListener> packet) {
//        for (var client : clients) {
//            if (client == clientException) continue;
//            send(client, packet);
//        }
//    }

    public void sendAll(Packet<? extends ClientPacketListener> packet) {
        send(this.clients, packet);
    }

    public void forceDisconnect(ClientConnectionHandler client) {
        try {
            client.stop();
        } catch (IOException e) {
            listener.exceptionOccurred(client, e);
        }
        clients.remove(client);
    }
}
