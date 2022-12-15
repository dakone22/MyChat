package core.network.packets.s2c.chat;

import core.User;
import core.network.fields.EnumField;
import core.network.fields.Field;
import core.network.listeners.ClientPacketListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;

public class ClientLeaveS2CPacket extends ClientJoinS2CPacket {
    public enum DisconnectReason {
        SelfDisconnect, Error
    }

    private final Field<DisconnectReason> disconnectReasonField = fields.add(
            new EnumField.Builder<>(DisconnectReason.class).build());

    public ClientLeaveS2CPacket(ObjectInputStream istream) throws IOException {
        read(istream);
    }

    public ClientLeaveS2CPacket(Integer index, LocalDateTime dateTime, User user, DisconnectReason reason) {
        super(index, dateTime, user);
        this.disconnectReasonField.setValue(reason);
    }

    public DisconnectReason getDisconnectReason() {
        return disconnectReasonField.getValue();
    }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onClientLeave(this);
    }
}
