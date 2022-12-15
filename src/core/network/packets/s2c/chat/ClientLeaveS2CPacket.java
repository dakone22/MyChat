package core.network.packets.s2c.chat;

import core.User;
import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

public record ClientLeaveS2CPacket(User user, DisconnectReason reason) implements Packet<ClientPacketListener> {
    public enum DisconnectReason {
        SelfDisconnect, Error
    }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onClientLeave(this);
    }
}
