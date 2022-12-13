package core.network.packets.s2c.chat;

import core.network.listeners.ClientChatPacketListener;
import core.network.packets.Packet;
import core.network.packets.PacketByteBuffer;

public class ClientJoinS2CPacket implements Packet<ClientChatPacketListener> {
    private final String client;  // TODO: ClientClass

    public ClientJoinS2CPacket(String client) {
        this.client = client;
    }

    public ClientJoinS2CPacket(PacketByteBuffer buf) {
        this.client = buf.readString();
    }

    @Override
    public void write(PacketByteBuffer buf) {

    }

    @Override
    public void apply(ClientChatPacketListener listener) {

    }
}
