package core.network.packets.s2c;

import core.network.listeners.ClientPacketListener;
import core.network.fields.Field;
import core.network.fields.StringField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;

public class ClientJoinS2CPacket extends MessageS2CPacket {
    private final Field<String> client = fields.add(new StringField.Builder().build());  // TODO: ClientField

    protected ClientJoinS2CPacket() { }
    public ClientJoinS2CPacket(ObjectInputStream istream) throws IOException { read(istream); }
    public ClientJoinS2CPacket(Integer index, LocalDateTime dateTime, String client) {
        super(index, dateTime);
        this.client.setValue(client);
    }

    public String getClient() {
        return client.getValue();
    }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onClientJoin(this);
    }
}
