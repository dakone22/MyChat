package core.network.packets.c2s.chat;

import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;

public record PublicChatMessageC2SPacket(String message) implements Packet<ServerPacketListener> {
    @Override
    public void apply(ServerPacketListener listener) {
        listener.onPublicMessage(this);
    }
}
