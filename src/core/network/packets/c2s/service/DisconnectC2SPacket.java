package core.network.packets.c2s.service;

import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;

public class DisconnectC2SPacket extends Packet<ServerPacketListener> {
    public DisconnectC2SPacket() {
    }

    public DisconnectC2SPacket(ObjectInputStream istream) throws IOException {
        read(istream);
    }

    @Override
    public void apply(ServerPacketListener listener) {
        listener.onDisconnect(this);
    }
}
