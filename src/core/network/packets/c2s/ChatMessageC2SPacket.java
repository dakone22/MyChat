package core.network.packets.c2s;

import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;
import core.network.fields.Field;
import core.network.fields.StringField;

import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class ChatMessageC2SPacket extends Packet<ServerPacketListener> {
    private final Field<String> receiver = fields.add(new StringField.Builder().build());  // TODO: ClientField
    private final Field<String> message = fields.add(new StringField.Builder().build());

    protected ChatMessageC2SPacket(String receiver, String message) {
        this.receiver.setValue(receiver);
        this.message.setValue(message);
    }

    protected ChatMessageC2SPacket() { }

    static public class Private extends ChatMessageC2SPacket {
        public Private(String receiver, String message) {
            super(receiver, message);
        }

        public Private(ObjectInputStream istream) throws IOException { read(istream); }

        @Override
        public void apply(ServerPacketListener listener) {
            listener.onPrivateMessage(this);
        }
    }

    static public class Public extends ChatMessageC2SPacket {
        public Public(String message) {
            super("", message);
        }

        public Public(ObjectInputStream istream) throws IOException { read(istream); }

        @Override
        public void apply(ServerPacketListener listener) {
            listener.onPublicMessage(this);
        }
    }

    public String getReceiver() {
        return receiver.getValue();
    }

    public String getMessage() {
        return message.getValue();
    }
}
