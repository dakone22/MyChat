package core.network.packets.c2s.chat;

import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;
import core.network.packets.fields.Field;
import core.network.packets.fields.StringField;

public abstract class ChatMessageC2SPacket extends Packet<ServerPacketListener> {
    private final Field<String> receiver = new StringField.Builder().build();  // TODO: ClientField
    private final Field<String> message = new StringField.Builder().build();

    protected ChatMessageC2SPacket(String receiver, String message) {
        this.receiver.setValue(receiver);
        this.message.setValue(message);
    }

    static public class Private extends ChatMessageC2SPacket {
        public Private(String receiver, String message) {
            super(receiver, message);
        }

        @Override
        public void apply(ServerPacketListener listener) {
            listener.onPrivateMessage(this);
        }
    }

    static public class Public extends ChatMessageC2SPacket {
        public Public(String message) {
            super("", message);
        }

        @Override
        protected void apply(ServerPacketListener listener) {
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
