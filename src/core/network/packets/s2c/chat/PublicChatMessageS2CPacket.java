package core.network.packets.s2c.chat;

import core.User;
import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

import java.time.LocalDateTime;

public record PublicChatMessageS2CPacket(User sender, String message) implements Packet<ClientPacketListener> {
    @Override
    public void apply(ClientPacketListener listener) {
        listener.onPublicMessage(this);
    }
}
