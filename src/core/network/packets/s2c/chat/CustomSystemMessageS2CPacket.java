package core.network.packets.s2c.chat;

import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

public record CustomSystemMessageS2CPacket(String message) implements Packet<ClientPacketListener> {
    @Override
    public void apply(ClientPacketListener listener) {
        listener.onCustomSystemMessage(this);
    }
}
