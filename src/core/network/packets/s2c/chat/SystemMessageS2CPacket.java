package core.network.packets.s2c.chat;

import core.network.fields.EnumField;
import core.network.fields.Field;
import core.network.listeners.ClientPacketListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;

public class SystemMessageS2CPacket extends MessageS2CPacket {
    public enum SystemMessageType {
        PromotedToAdmin, PromotedToModerator,
        // ...
    }

    private final Field<SystemMessageType> messageTypeField = fields.add(new EnumField.Builder<SystemMessageType>(SystemMessageType.class).build());

    public SystemMessageType getMessageType() {
        return messageTypeField.getValue();
    }

    public SystemMessageS2CPacket(Integer index, LocalDateTime dateTime, SystemMessageType type) {
        super(index, dateTime);
        this.messageTypeField.setValue(type);
    }

    public SystemMessageS2CPacket(ObjectInputStream stream) throws IOException { read(stream); }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onSystemMessage(this);
    }
}
