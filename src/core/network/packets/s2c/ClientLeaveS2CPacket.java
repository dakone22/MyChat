package core.network.packets.s2c;

import core.network.listeners.ClientPacketListener;
import core.network.fields.EnumField;
import core.network.fields.Field;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;

public class ClientLeaveS2CPacket extends ClientJoinS2CPacket {
    public enum DisconnectReason {
        SelfDisconnect, Error
    }

    private final Field<DisconnectReason> disconnectReason = fields.add(
            new EnumField.Builder<DisconnectReason>(DisconnectReason.class).build());

    public ClientLeaveS2CPacket(ObjectInputStream istream) throws IOException { read(istream); }
    public ClientLeaveS2CPacket(Integer index, LocalDateTime dateTime, String client, DisconnectReason reason) {
        super(index, dateTime, client);
        this.disconnectReason.setValue(reason);
    }

    public DisconnectReason getDisconnectReason() {
        return disconnectReason.getValue();
    }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onClientLeave(this);
    }
}
