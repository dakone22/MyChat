package core.network.packets.s2c.chat;

import core.User;
import core.network.fields.Field;
import core.network.fields.StringField;
import core.network.fields.UserField;
import core.network.listeners.ClientPacketListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;

public abstract class ChatMessageS2CPacket extends MessageS2CPacket {
    private final Field<User> senderField = fields.add(new UserField.Builder().build());
    private final Field<String> messageField = fields.add(new StringField.Builder().build());

    protected ChatMessageS2CPacket(int index, LocalDateTime dateTime, User sender, String message) {
        super(index, dateTime);
        this.senderField.setValue(sender);
        this.messageField.setValue(message);
    }

    protected ChatMessageS2CPacket() {
    }

    static public class Private extends ChatMessageS2CPacket {
        private final Field<User> receiverField = fields.add(new UserField.Builder().build());

        public Private(int index, LocalDateTime dateTime, User sender, User receiver, String message) {
            super(index, dateTime, sender, message);
            this.receiverField.setValue(receiver);
        }

        public Private(ObjectInputStream istream) throws IOException {
            read(istream);
        }

        @Override
        public void apply(ClientPacketListener listener) {
            listener.onPrivateMessage(this);
        }

        public User getReceiver() {
            return receiverField.getValue();
        }

    }

    static public class Public extends ChatMessageS2CPacket {
        public Public(int index, LocalDateTime dateTime, User sender, String message) {
            super(index, dateTime, sender, message);
        }

        public Public(ObjectInputStream istream) throws IOException {
            read(istream);
        }

        @Override
        public void apply(ClientPacketListener listener) {
            listener.onPublicMessage(this);
        }
    }

    public User getSender() {
        return senderField.getValue();
    }

    public String getMessage() {
        return messageField.getValue();
    }

}
