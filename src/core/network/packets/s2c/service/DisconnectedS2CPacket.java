package core.network.packets.s2c.service;

import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

public record DisconnectedS2CPacket(DisconnectReason reason) implements Packet<ClientPacketListener> {
    public enum DisconnectReason {
        ServerClosed, Timeout
    }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onDisconnected(this);
    }
}
