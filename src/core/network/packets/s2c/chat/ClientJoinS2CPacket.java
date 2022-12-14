package core.network.packets.s2c.chat;

import core.network.listeners.ClientPacketListener;
import core.network.packets.fields.Field;
import core.network.packets.fields.StringField;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

public class ClientJoinS2CPacket extends MessageS2CPacket {
    private final Field<String> client = fields.add(new StringField.Builder().build());  // TODO: ClientField

    public ClientJoinS2CPacket(ByteBuffer buffer) { super(buffer); }
    public ClientJoinS2CPacket(Integer index, LocalDateTime dateTime, String client) {
        super(index, dateTime);
        this.client.setValue(client);
    }

    public String getClient() {
        return client.getValue();
    }

    @Override
    protected void apply(ClientPacketListener listener) {
        listener.onClientJoin(this);
    }
}
