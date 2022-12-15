package core.network.packets.s2c.chat;

import core.User;
import core.network.fields.Field;
import core.network.fields.UserField;
import core.network.listeners.ClientPacketListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;

public class ClientJoinS2CPacket extends MessageS2CPacket {
    private final Field<User> userField = fields.add(new UserField.Builder().build());

    protected ClientJoinS2CPacket() {
    }

    public ClientJoinS2CPacket(ObjectInputStream istream) throws IOException {
        read(istream);
    }

    public ClientJoinS2CPacket(Integer index, LocalDateTime dateTime, User user) {
        super(index, dateTime);
        this.userField.setValue(user);
    }

    public User getUser() {
        return userField.getValue();
    }

    @Override
    public void apply(ClientPacketListener listener) {
        listener.onClientJoin(this);
    }
}
