package core.network.packets.s2c.chat;

import core.User;
import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

public record ClientLeaveS2CPacket(User user, LeaveReason reason) implements Packet<ClientPacketListener> {
    public enum LeaveReason {
        SelfDisconnect, Kicked, ErrorOccurred
    }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onClientLeave(this);
    }
}
