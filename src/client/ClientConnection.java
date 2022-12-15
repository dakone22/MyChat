package client;

import core.Connection;
import core.network.listeners.ClientPacketListener;
import core.network.listeners.ServerPacketListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static core.network.PacketHandler.C2S_PACKET_HANDLER;
import static core.network.PacketHandler.S2C_PACKET_HANDLER;

public class ClientConnection extends Connection<ClientPacketListener, ServerPacketListener> {

    private final ClientConnectionListener clientPacketListener;

    public ClientConnection(ClientConnectionListener listener) {
        this.clientPacketListener = listener;
    }

    public void connect(InetAddress host, int port) throws IOException {
        // TODO: if (connected) return;

        socket = new Socket(host, port);
        startConnection(socket, C2S_PACKET_HANDLER, S2C_PACKET_HANDLER, clientPacketListener);
    }
}
