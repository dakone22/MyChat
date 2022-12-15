package core.network.packets.c2s.chat;

import core.User;
import core.network.fields.Field;
import core.network.fields.StringField;
import core.network.fields.UserField;
import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class ChatMessageC2SPacket extends Packet<ServerPacketListener> {
    private final Field<String> messageField = fields.add(new StringField.Builder().build());

    protected ChatMessageC2SPacket(String message) {
        this.messageField.setValue(message);
    }

    protected ChatMessageC2SPacket() {
    }

    static public class Private extends ChatMessageC2SPacket {
        private final Field<User> receiver = fields.add(new UserField.Builder().build());

        public Private(User receiver, String message) {
            super(message);
            this.receiver.setValue(receiver);
        }

        public Private(ObjectInputStream istream) throws IOException {
            read(istream);
        }

        @Override
        public void apply(ServerPacketListener listener) {
            listener.onPrivateMessage(this);
        }

        public User getReceiver() {
            return receiver.getValue();
        }
    }

    static public class Public extends ChatMessageC2SPacket {
        public Public(String message) {
            super(message);
        }

        public Public(ObjectInputStream istream) throws IOException {
            read(istream);
        }

        @Override
        public void apply(ServerPacketListener listener) {
            listener.onPublicMessage(this);
        }
    }

    public String getMessage() {
        return messageField.getValue();
    }
}
