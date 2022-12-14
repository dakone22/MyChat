package core.network.listeners;

import core.network.packets.s2c.login.LoginHelloS2CPacket;

@Deprecated
public interface ClientLoginPacketListener extends PacketListener {
    void onHello(LoginHelloS2CPacket packet);
}
