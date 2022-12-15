package core.network.listeners;

import core.network.packets.c2s.chat.ChatMessageC2SPacket;
import core.network.packets.c2s.chat.ChatUserListC2SPacket;
import core.network.packets.c2s.service.DisconnectC2SPacket;
import core.network.packets.c2s.service.HandshakeC2SPacket;

public interface ServerPacketListener extends PacketListener {
    void onPublicMessage(ChatMessageC2SPacket.Public packet);

    void onPrivateMessage(ChatMessageC2SPacket.Private packet);

    void onHandshake(HandshakeC2SPacket packet);

    void onDisconnect(DisconnectC2SPacket packet);

    void onUserListRequest(ChatUserListC2SPacket packet);
}
