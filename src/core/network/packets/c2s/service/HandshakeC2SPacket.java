package core.network.packets.c2s.service;

import core.User;
import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;

public record HandshakeC2SPacket(User requestedUserData) implements Packet<ServerPacketListener> {

    @Override
    public void apply(ServerPacketListener listener) {
        listener.onHandshake(this);
    }
}
