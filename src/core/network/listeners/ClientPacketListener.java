package core.network.listeners;

import core.network.packets.s2c.chat.*;
import core.network.packets.s2c.service.ConnectedFailureS2CPacket;
import core.network.packets.s2c.service.ConnectedSuccessS2CPacket;
import core.network.packets.s2c.service.DisconnectedS2CPacket;

public interface ClientPacketListener extends PacketListener {
    void onPublicMessage(ChatMessageS2CPacket.Public packet);

    void onPrivateMessage(ChatMessageS2CPacket.Private packet);

    void onSystemMessage(SystemMessageS2CPacket packet);

    void onCustomSystemMessage(CustomSystemMessageS2CPacket packet);

    void onClientJoin(ClientJoinS2CPacket packet);

    void onClientLeave(ClientLeaveS2CPacket packet);

    void onConnected(ConnectedSuccessS2CPacket packet);

    void onConnectFailed(ConnectedFailureS2CPacket packet);

    void onDisconnected(DisconnectedS2CPacket packet);

    void onUserListRequest(ChatUserListS2CPacket packet);
}
