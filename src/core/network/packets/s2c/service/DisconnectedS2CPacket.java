package core.network.packets.s2c.service;

import core.network.fields.EnumField;
import core.network.fields.Field;
import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;

public class DisconnectedS2CPacket extends Packet<ClientPacketListener> {
    public enum DisconnectReason {
        ServerClosed, Timeout
    }

    private final Field<DisconnectReason> disconnectReasonField = fields.add(new EnumField.Builder<>(DisconnectReason.class).build());

    public DisconnectedS2CPacket(DisconnectReason reason) {
        this.disconnectReasonField.setValue(reason);
    }

    public DisconnectedS2CPacket(ObjectInputStream istream) throws IOException {
        read(istream);
    }

    public DisconnectReason getDisconnectReason() {
        return disconnectReasonField.getValue();
    }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onDisconnected(this);
    }
}
