package core.network.packets.s2c.chat;

import core.network.listeners.ClientPacketListener;
import core.network.packets.fields.EnumField;
import core.network.packets.fields.Field;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

public class SystemMessageS2CPacket extends MessageS2CPacket {
    public enum SystemMessageType {
        PromotedToAdmin, PromotedToModerator,
    }

    private final Field<SystemMessageType> messageType = new EnumField.Builder<SystemMessageType>(SystemMessageType.class).build();

    public SystemMessageType getMessageType() {
        return messageType.getValue();
    }

    public SystemMessageS2CPacket(Integer index, LocalDateTime dateTime, SystemMessageType type) {
        super(index, dateTime);
        this.messageType.setValue(type);
    }

    public SystemMessageS2CPacket(ByteBuffer buffer) { super(buffer); }

    @Override
    protected void apply(ClientPacketListener listener) {
        listener.onSystemMessage(this);
    }
}
