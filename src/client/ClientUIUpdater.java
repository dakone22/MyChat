package client;

import core.ExceptionOccurredListener;
import core.User;
import core.network.packets.s2c.chat.ClientLeaveS2CPacket;
import core.network.packets.s2c.chat.SystemMessageS2CPacket;
import core.network.packets.s2c.service.ConnectedFailureS2CPacket;
import core.network.packets.s2c.service.DisconnectedS2CPacket;

public interface ClientUIUpdater extends ExceptionOccurredListener {
    void onCustomMessage(String message);

    void publicMessage(User sender, String message);

    void privateMessage(User sender, User receiver, String message);

    void clientJoined(User user);

    void clientLeft(User user, ClientLeaveS2CPacket.LeaveReason reason);

    void connected(User assignedUser);

    void updateUserList(Iterable<User> userList);

    void forceDisconnected(DisconnectedS2CPacket.DisconnectReason reason);

    void systemMessage(SystemMessageS2CPacket.MessageType messageType);

    void connectFailed(ConnectedFailureS2CPacket.FailReason failReason);

    void disconnected();
}
