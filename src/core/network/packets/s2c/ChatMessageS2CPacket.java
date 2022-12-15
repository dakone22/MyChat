package core.network.packets.s2c;

import core.network.fields.Field;
import core.network.fields.StringField;
import core.network.listeners.ClientPacketListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;

public abstract class ChatMessageS2CPacket extends MessageS2CPacket {
    private final Field<String> sender = fields.add(new StringField.Builder().build());    // TODO: ClientField
    private final Field<String> receiver = fields.add(new StringField.Builder().build());  // TODO: ClientField
    private final Field<String> message = fields.add(new StringField.Builder().build());

    protected ChatMessageS2CPacket(int index, LocalDateTime dateTime, String sender, String receiver, String message) {
        super(index, dateTime);
        this.sender.setValue(sender);
        this.receiver.setValue(receiver);
        this.message.setValue(message);
    }

    protected ChatMessageS2CPacket() { }

    static public class Private extends ChatMessageS2CPacket {
        public Private(int index, LocalDateTime dateTime, String sender, String receiver, String message) {
            super(index, dateTime, sender, receiver, message);
        }

        public Private(ObjectInputStream istream) throws IOException {
            read(istream);
        }

        @Override
        public void apply(ClientPacketListener listener) {
            listener.onPrivateMessage(this);
        }
    }

    static public class Public extends ChatMessageS2CPacket {
        public Public(int index, LocalDateTime dateTime, String sender, String message) {
            super(index, dateTime, sender, "", message);
        }

        public Public(ObjectInputStream istream) throws IOException { read(istream); }

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
