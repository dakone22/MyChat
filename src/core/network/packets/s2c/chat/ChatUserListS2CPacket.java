package core.network.packets.s2c.chat;

import core.User;
import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

import java.util.List;

public record ChatUserListS2CPacket(List<User> userList) implements Packet<ClientPacketListener> {
    @Override
    public void apply(ClientPacketListener listener) {
        listener.onUserListRequest(this);
    }
}
