package core.network.packets.s2c.service;

import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

public record LoginRequestS2CPacket() implements Packet<ClientPacketListener> {
    @Override
    public void apply(ClientPacketListener listener) {
        listener.onLoginRequest(this);
    }
}
