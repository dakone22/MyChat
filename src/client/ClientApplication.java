package client;

import core.User;
import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;
import core.network.packets.c2s.chat.ChatUserListC2SPacket;
import core.network.packets.c2s.chat.PrivateChatMessageC2SPacket;
import core.network.packets.c2s.chat.PublicChatMessageC2SPacket;
import core.network.packets.c2s.service.DisconnectC2SPacket;
import core.network.packets.s2c.chat.*;
import core.network.packets.s2c.service.ConnectedFailureS2CPacket;
import core.network.packets.s2c.service.ConnectedSuccessS2CPacket;
import core.network.packets.s2c.service.DisconnectedS2CPacket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ClientApplication {
    private final ClientApplicationListener output;
    private final ClientConnection networkHandler;

    private User currentUserData;
    private List<User> userList;
    private boolean isConnected = false;

    public ClientApplication(ClientApplicationListener clientApplicationListener) {
        output = clientApplicationListener;

        networkHandler = new ClientConnection(setupConnectionListener());
        networkHandler.addExceptionOccurredListener(output);
    }

    private ClientConnectionListener setupConnectionListener() {
        return new ClientConnectionListener() {
            @Override
            public void onPublicMessage(PublicChatMessageS2CPacket packet) {
                // TODO: check sender
                output.publicMessage(packet.sender(), packet.message());
            }

            @Override
            public void onPrivateMessage(PrivateChatMessageS2CPacket packet) {
                // TODO: check sender & receiver
                output.privateMessage(packet.sender(), packet.receiver(), packet.message());
            }

            @Override
            public void onSystemMessage(SystemMessageS2CPacket packet) {
                // TODO: SystemMessage
                output.systemMessage(packet.messageType());
            }

            @Override
            public void onCustomSystemMessage(CustomSystemMessageS2CPacket packet) {
                output.onCustomMessage(packet.message());
            }

            @Override
            public void onClientJoin(ClientJoinS2CPacket packet) {
                requestUserList();
                output.clientJoined(packet.user());
            }

            @Override
            public void onClientLeave(ClientLeaveS2CPacket packet) {
                requestUserList();
                output.clientLeft(packet.user(), packet.reason());
            }

            @Override
            public void onConnected(ConnectedSuccessS2CPacket packet) {
                currentUserData = packet.assignedUser();
                isConnected = true;

                requestUserList();
                output.connected(packet.assignedUser());
            }

            @Override
            public void onConnectFailed(ConnectedFailureS2CPacket packet) {
                output.connectFailed(packet.failReason());
                stop();
            }

            @Override
            public void onDisconnected(DisconnectedS2CPacket packet) {
                output.disconnected(packet.reason());
                stop();
            }

            @Override
            public void onUserListRequest(ChatUserListS2CPacket packet) {
                userList = packet.userList();
                List<User> filteredUserList = new ArrayList<>(userList);
                filteredUserList.remove(currentUserData);
                output.updateUserList(filteredUserList);
            }
        };
    }

    private void sendPacket(Packet<? extends ServerPacketListener> packet) {
        try {
            networkHandler.sendPacket(packet);
        } catch (Exception e) {
            output.exceptionOccurred(this, e);
        }
    }

    private void stop() {
        try {
            networkHandler.stop();
            isConnected = false;
        } catch (Exception e) {
            output.exceptionOccurred(this, e);
        }
    }

    private void requestUserList() {
        sendPacket(new ChatUserListC2SPacket());
    }

    public void connect(String host, int port) {
        if (isConnected) return;
        try {
            networkHandler.connect(InetAddress.getByName(host), port);
        } catch (Exception e) {
            output.exceptionOccurred(this, e);
        }
    }

    public void disconnect() {
        if (!isConnected) return;
        sendPacket(new DisconnectC2SPacket());
        stop();
    }

    public void sendPublicMessage(String message) {
        sendPacket(new PublicChatMessageC2SPacket(message));
    }

    public void sendPrivateMessage(User toUser, String message) {
        sendPacket(new PrivateChatMessageC2SPacket(toUser, message));
    }

}
