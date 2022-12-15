package client;

import core.ExceptionOccurredListener;
import core.User;
import core.network.packets.s2c.chat.ClientLeaveS2CPacket;
import core.network.packets.s2c.chat.SystemMessageS2CPacket;
import core.network.packets.s2c.service.ConnectedFailureS2CPacket;
import core.network.packets.s2c.service.DisconnectedS2CPacket;

import java.util.List;

public interface ClientApplicationListener extends ExceptionOccurredListener {
    void onCustomMessage(String message);

    void publicMessage(User sender, String message);

    void privateMessage(User sender, User receiver, String message);

    void clientJoined(User user);

    void clientLeft(User user, ClientLeaveS2CPacket.DisconnectReason reason);

    void connected(User assignedUser);

    void updateUserList(List<User> userList);

    void disconnected(DisconnectedS2CPacket.DisconnectReason reason);

    void systemMessage(SystemMessageS2CPacket.MessageType messageType);

    void connectFailed(ConnectedFailureS2CPacket.FailReason failReason);
}
