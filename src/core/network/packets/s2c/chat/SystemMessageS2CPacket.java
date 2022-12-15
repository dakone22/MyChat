package core.network.packets.s2c.chat;

import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

@Deprecated
public record SystemMessageS2CPacket(MessageType messageType) implements Packet<ClientPacketListener> {
    public enum MessageType {
        PromotedToAdmin, PromotedToModerator,
        // ...
    }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onSystemMessage(this);
    }
}
