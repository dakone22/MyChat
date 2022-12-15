package core.network.packets.s2c;

import core.network.listeners.ClientPacketListener;
import core.network.fields.EnumField;
import core.network.fields.Field;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;

public class SystemMessageS2CPacket extends MessageS2CPacket {
    public enum SystemMessageType {
        PromotedToAdmin, PromotedToModerator,
    }

    private final Field<SystemMessageType> messageType = fields.add(new EnumField.Builder<SystemMessageType>(SystemMessageType.class).build());

    public SystemMessageType getMessageType() {
        return messageType.getValue();
    }

    public SystemMessageS2CPacket(Integer index, LocalDateTime dateTime, SystemMessageType type) {
        super(index, dateTime);
        this.messageType.setValue(type);
    }

    public SystemMessageS2CPacket(ObjectInputStream stream) throws IOException { read(stream); }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onSystemMessage(this);
    }
}
