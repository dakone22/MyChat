package core.network.packets.s2c.chat;

import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

public record SystemMessageS2CPacket(MessageType messageType) implements Packet<ClientPacketListener> {
    public enum MessageType {
        PromotedToAdmin, PromotedToModerator, ErrorNoSuchUser
        // ...
    }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onSystemMessage(this);
    }
}
