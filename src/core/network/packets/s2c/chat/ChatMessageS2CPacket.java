package core.network.packets.s2c.chat;

import core.network.listeners.ClientPacketListener;
import core.network.packets.fields.Field;
import core.network.packets.fields.StringField;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

public abstract class ChatMessageS2CPacket extends MessageS2CPacket {
    private final Field<String> sender = new StringField.Builder().build();    // TODO: ClientField
    private final Field<String> receiver = new StringField.Builder().build();  // TODO: ClientField
    private final Field<String> message = new StringField.Builder().build();

    protected ChatMessageS2CPacket(int index, LocalDateTime dateTime, String sender, String receiver, String message) {
        super(index, dateTime);
        this.sender.setValue(sender);
        this.receiver.setValue(receiver);
        this.message.setValue(message);
    }

    protected ChatMessageS2CPacket(ByteBuffer buffer) { super(buffer); }

    static public class Private extends ChatMessageS2CPacket {
        public Private(int index, LocalDateTime dateTime, String sender, String receiver, String message) {
            super(index, dateTime, sender, receiver, message);
        }

        public Private(ByteBuffer buffer) { super(buffer); }

        @Override
        public void apply(ClientPacketListener listener) {
            listener.onPrivateMessage(this);
        }
    }

    static public class Public extends ChatMessageS2CPacket {
        public Public(int index, LocalDateTime dateTime, String sender, String message) {
            super(index, dateTime, sender, "", message);
        }

        public Public(ByteBuffer buffer) { super(buffer); }

        @Override
        public void apply(ClientPacketListener listener) {
            listener.onPublicMessage(this);
        }
    }

    public String getSender() {
        return sender.getValue();
    }

    public String getReceiver() {
        return receiver.getValue();
    }

    public String getMessage() {
        return message.getValue();
    }

}
