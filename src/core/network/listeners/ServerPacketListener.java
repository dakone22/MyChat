package core.network.listeners;

import core.network.packets.c2s.chat.ChatUserListC2SPacket;
import core.network.packets.c2s.chat.PrivateChatMessageC2SPacket;
import core.network.packets.c2s.chat.PublicChatMessageC2SPacket;
import core.network.packets.c2s.service.DisconnectC2SPacket;
import core.network.packets.c2s.service.LoginC2SPacket;

public interface ServerPacketListener extends PacketListener {
    void onPublicMessage(PublicChatMessageC2SPacket packet);

    void onPrivateMessage(PrivateChatMessageC2SPacket packet);

    void onLogin(LoginC2SPacket packet);

    void onDisconnect(DisconnectC2SPacket packet);

    void onUserListRequest(ChatUserListC2SPacket packet);
}
