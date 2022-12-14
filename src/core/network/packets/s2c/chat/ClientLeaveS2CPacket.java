package core.network.packets.s2c.chat;

import core.network.listeners.ClientPacketListener;
import core.network.packets.fields.EnumField;
import core.network.packets.fields.Field;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

public class ClientLeaveS2CPacket extends ClientJoinS2CPacket {
    public enum DisconnectReason {
        SelfDisconnect, Error
    }

    private final Field<DisconnectReason> disconnectReason = fields.add(
            new EnumField.Builder<DisconnectReason>(DisconnectReason.class).build());

    public ClientLeaveS2CPacket(ByteBuffer buffer) { super(buffer); }
    public ClientLeaveS2CPacket(Integer index, LocalDateTime dateTime, String client, DisconnectReason reason) {
        super(index, dateTime, client);
        this.disconnectReason.setValue(reason);
    }

    public DisconnectReason getDisconnectReason() {
        return disconnectReason.getValue();
    }

    @Override
    protected void apply(ClientPacketListener listener) {
        listener.onClientLeave(this);
    }
}
