package core.network.packets.c2s.chat;

import core.User;
import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;

public record PrivateChatMessageC2SPacket(User receiver, String message) implements Packet<ServerPacketListener> {
    @Override
    public void apply(ServerPacketListener listener) {
        listener.onPrivateMessage(this);
    }
}
