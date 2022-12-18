package core.network.packets.s2c.chat;

import core.User;
import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

import java.util.Collection;

public record ChatUserListS2CPacket(Collection<User> userList) implements Packet<ClientPacketListener> {
    @Override
    public void apply(ClientPacketListener listener) {
        listener.onUserListResponse(this);
    }
}
