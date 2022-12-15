package server;

import core.Connection;
import core.network.listeners.ClientPacketListener;
import core.network.listeners.ServerPacketListener;

import java.io.IOException;
import java.net.Socket;

import static core.network.PacketHandler.C2S_PACKET_HANDLER;
import static core.network.PacketHandler.S2C_PACKET_HANDLER;

public class ClientConnectionHandler extends Connection<ServerPacketListener, ClientPacketListener> {

    public ClientConnectionHandler(Socket clientSocket) {
        socket = clientSocket;
    }

    public void start(ServerPacketListener listener) throws IOException {
        startConnection(socket, S2C_PACKET_HANDLER, C2S_PACKET_HANDLER, listener);
    }

}
