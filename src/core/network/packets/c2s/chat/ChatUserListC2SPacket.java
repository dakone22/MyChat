package core.network.packets.c2s.chat;

import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ChatUserListC2SPacket extends Packet<ServerPacketListener> {
    public ChatUserListC2SPacket() {
    }

    public ChatUserListC2SPacket(ObjectInputStream istream) throws IOException {
        read(istream);
    }

    @Override
    public void apply(ServerPacketListener listener) {
        listener.onUserListRequest(this);
    }
}
