package core.network.listeners;

import core.network.packets.s2c.login.LoginHelloS2CPacket;

public interface ClientLoginPacketListener extends ClientPacketListener {
    void onHello(LoginHelloS2CPacket packet);
}
