package core.network.packets.s2c.chat;

import core.network.fields.Field;
import core.network.fields.StringField;
import core.network.listeners.ClientPacketListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;

@Deprecated
public class CustomSystemMessageS2CPacket extends MessageS2CPacket {
    private final Field<String> messageField = fields.add(new StringField.Builder().build());

    public String getMessage() { return messageField.getValue(); }

    public CustomSystemMessageS2CPacket(Integer index, LocalDateTime dateTime, String message) {
        super(index, dateTime);
        this.messageField.setValue(message);
    }

    public CustomSystemMessageS2CPacket(ObjectInputStream istream) throws IOException { read(istream); }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onCustomSystemMessage(this);
    }
}
