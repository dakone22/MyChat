package core.network.packets.s2c.login;

import core.network.listeners.ClientLoginPacketListener;
import core.network.packets.Packet;
import core.network.packets.PacketByteBuffer;

public class LoginHelloS2CPacket implements Packet<ClientLoginPacketListener> {  // TODO: handshake
    private final Integer serverId;

    public LoginHelloS2CPacket(Integer serverId) {
        this.serverId = serverId;
    }

    @Override
    public void write(PacketByteBuffer buf) {
        buf.writeInt(this.serverId);
    }

    @Override
    public void apply(ClientLoginPacketListener listener) {
        listener.onHello(this);
    }
}
