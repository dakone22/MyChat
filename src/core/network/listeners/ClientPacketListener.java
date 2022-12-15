package core.network.listeners;

import core.network.packets.s2c.*;

public interface ClientPacketListener extends PacketListener {
    void onPublicMessage(ChatMessageS2CPacket.Public packet);
    void onPrivateMessage(ChatMessageS2CPacket.Private packet);
    void onSystemMessage(SystemMessageS2CPacket packet);
    void onCustomSystemMessage(CustomSystemMessageS2CPacket packet);
    void onClientJoin(ClientJoinS2CPacket packet);
    void onClientLeave(ClientLeaveS2CPacket packet);
}
