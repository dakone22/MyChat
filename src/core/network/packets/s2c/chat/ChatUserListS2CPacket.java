package core.network.packets.s2c.chat;

import core.User;
import core.network.fields.Field;
import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

import java.util.List;

public class ChatUserListS2CPacket extends Packet<ClientPacketListener> {
    private final Field<List<User>> userListField = fields.add(new ListField);
    @Override
    public void apply(ClientPacketListener listener) {
        listener.onUserListRequest(this);
    }
}
