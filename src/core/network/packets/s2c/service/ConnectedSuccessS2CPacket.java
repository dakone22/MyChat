package core.network.packets.s2c.service;

import core.User;
import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

public record ConnectedSuccessS2CPacket(User assignedUser) implements Packet<ClientPacketListener> {
    @Override
    public void apply(ClientPacketListener listener) {
        listener.onConnected(this);
    }

}
