package core.network.listeners;

import core.network.packets.s2c.chat.MessageS2CPacket;

public interface ClientChatPacketListener extends ClientPacketListener {
    void onPublicMessage(MessageS2CPacket packet);
    void onPrivateMessage(PrivateMessageS2CPacket packet);
    void onServerMessage(ServerMessageS2CPacket packet);
}
