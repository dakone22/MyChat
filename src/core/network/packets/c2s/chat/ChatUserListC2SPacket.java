package core.network.packets.c2s.chat;

import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;

public class ChatUserListC2SPacket implements Packet<ServerPacketListener> {
    @Override
    public void apply(ServerPacketListener listener) {
        listener.onUserListRequest(this);
    }
}
