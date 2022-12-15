package core.network.packets.s2c.service;

import core.network.fields.EnumField;
import core.network.fields.Field;
import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ConnectedFailureS2CPacket extends Packet<ClientPacketListener> {

    public enum FailReason {
        UsernameAlreadyTaken, Banned,
    }

    private final Field<FailReason> failReasonField = fields.add(new EnumField.Builder<>(FailReason.class).build());

    public ConnectedFailureS2CPacket(FailReason reason) {
        this.failReasonField.setValue(reason);
    }

    public ConnectedFailureS2CPacket(ObjectInputStream istream) throws IOException {
        read(istream);
    }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onConnectFailed(this);
    }

    public FailReason getFailReason() {
        return failReasonField.getValue();
    }
}
