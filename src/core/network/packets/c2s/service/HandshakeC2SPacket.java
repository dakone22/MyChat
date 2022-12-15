package core.network.packets.c2s.service;

import core.User;
import core.network.fields.Field;
import core.network.fields.UserField;
import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.UUID;

public class HandshakeC2SPacket extends Packet<ServerPacketListener> {

    private static final UUID EMPTY_UUID = new UUID(0, 0);
    private final Field<User> userField = fields.add(new UserField.Builder().build());

    public HandshakeC2SPacket(String username) {
        this.userField.setValue(new User(EMPTY_UUID, username));
    }

    public HandshakeC2SPacket(ObjectInputStream istream) throws IOException {
        read(istream);
    }

    @Override
    public void apply(ServerPacketListener listener) {
        listener.onHandshake(this);
    }

    public User getUser() {
        return userField.getValue();
    }
}
