package core.network.listeners;

import core.network.packets.c2s.chat.ChatMessageC2SPacket;

public interface ServerPacketListener extends PacketListener {
    void onPublicMessage(ChatMessageC2SPacket.Public packet);

    void onPrivateMessage(ChatMessageC2SPacket.Private packet);
}
