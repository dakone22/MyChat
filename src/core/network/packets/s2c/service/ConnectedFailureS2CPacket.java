package core.network.packets.s2c.service;

import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

public record ConnectedFailureS2CPacket(FailReason failReason) implements Packet<ClientPacketListener> {

    public enum FailReason {
        UsernameAlreadyTaken, Banned,
    }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onConnectFailed(this);
    }
}
