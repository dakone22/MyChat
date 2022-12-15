package core.network.packets.c2s.service;

import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;

public class DisconnectC2SPacket implements Packet<ServerPacketListener> {
    @Override
    public void apply(ServerPacketListener listener) {
        listener.onDisconnect(this);
    }
}
