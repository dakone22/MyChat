package core.network.packets.s2c.chat;

import core.User;
import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

public record ClientJoinS2CPacket(User user) implements Packet<ClientPacketListener> {
    @Override
    public void apply(ClientPacketListener listener) {
        listener.onClientJoin(this);
    }
}
