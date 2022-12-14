package core.network.listeners;

import core.network.packets.c2s.service.HandshakeC2SPacket;

@Deprecated
public interface ServerHandshakePacketListener extends ServerPacketListener, PacketListener {
    void onHandshake(HandshakeC2SPacket packet);
}
