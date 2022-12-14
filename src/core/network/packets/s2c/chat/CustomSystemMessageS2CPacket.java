package core.network.packets.s2c.chat;

import core.network.listeners.ClientPacketListener;
import core.network.packets.fields.Field;
import core.network.packets.fields.StringField;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

@Deprecated
public class CustomSystemMessageS2CPacket extends MessageS2CPacket {
    private final Field<String> message = new StringField.Builder().build();

    public String getMessage() { return message.getValue(); }

    public CustomSystemMessageS2CPacket(Integer index, LocalDateTime dateTime, String message) {
        super(index, dateTime);
        this.message.setValue(message);
    }

    public CustomSystemMessageS2CPacket(ByteBuffer buffer) { super(buffer); }

    @Override
    protected void apply(ClientPacketListener listener) {
        listener.onCustomSystemMessage(this);
    }
}
