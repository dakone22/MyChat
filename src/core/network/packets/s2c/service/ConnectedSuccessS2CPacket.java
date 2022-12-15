package core.network.packets.s2c.service;

import core.User;
import core.network.fields.Field;
import core.network.fields.UserField;
import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ConnectedSuccessS2CPacket extends Packet<ClientPacketListener> {
    private final Field<User> userField = fields.add(new UserField.Builder().build());

    public ConnectedSuccessS2CPacket(User assignedUser) {
        this.userField.setValue(assignedUser);
    }

    public ConnectedSuccessS2CPacket(ObjectInputStream istream) throws IOException {
        read(istream);
    }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onConnected(this);
    }

    public User getUser() {
        return userField.getValue();
    }
}
