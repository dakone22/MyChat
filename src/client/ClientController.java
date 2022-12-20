package client;

import core.PasswordHasher;
import core.User;
import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;
import core.network.packets.c2s.chat.ChatUserListC2SPacket;
import core.network.packets.c2s.chat.PrivateChatMessageC2SPacket;
import core.network.packets.c2s.chat.PublicChatMessageC2SPacket;
import core.network.packets.c2s.service.DisconnectC2SPacket;
import core.network.packets.c2s.service.LoginC2SPacket;
import core.network.packets.s2c.chat.*;
import core.network.packets.s2c.service.ConnectedFailureS2CPacket;
import core.network.packets.s2c.service.ConnectedSuccessS2CPacket;
import core.network.packets.s2c.service.DisconnectedS2CPacket;
import core.network.packets.s2c.service.LoginRequestS2CPacket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ClientController {
    private final ClientUIUpdater output;
    private final ClientNetwork networkHandler;

    private User currentUserData;
    private List<User> userList;
    private boolean isConnected = false;
    private String passwordHash;
    private String username;

    public ClientController(ClientUIUpdater uiUpdater) {
        output = uiUpdater;

        networkHandler = new ClientNetwork(setupConnectionListener());
        networkHandler.addExceptionOccurredListener((source, exception) -> {
            output.exceptionOccurred(source, exception);
            stop();
        });
    }

    private ClientPacketReceivable setupConnectionListener() {
        return new ClientPacketReceivable() {
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
                stop();
                output.connectFailed(packet.failReason());
            }

            @Override
            public void onDisconnected(DisconnectedS2CPacket packet) {
                stop();
                output.forceDisconnected(packet.reason());
            }

            @Override
            public void onUserListResponse(ChatUserListS2CPacket packet) {
                userList = List.copyOf(packet.userList());
                List<User> filteredUserList = new ArrayList<>(userList);
                filteredUserList.remove(currentUserData);
                output.updateUserList(filteredUserList);
            }

            @Override
            public void onLoginRequest(LoginRequestS2CPacket packet) {
                sendPacket(new LoginC2SPacket(new User(null, username), passwordHash));
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
        stop(false);
    }
    private void stop(boolean flush) {
        try {
            networkHandler.stop(flush);
            isConnected = false;
        } catch (Exception e) {
            output.exceptionOccurred(this, e);
        }
    }

    private void requestUserList() {
        sendPacket(new ChatUserListC2SPacket());
    }

    public void connect(String host, int port, String password, String username) {
        if (isConnected) return;
        try {
            this.passwordHash = PasswordHasher.getHashedPassword(password);
            this.username = username;
            networkHandler.connect(InetAddress.getByName(host), port);
        } catch (Exception e) {
            output.exceptionOccurred(this, e);
        }
    }

    public void softDisconnect() {
        if (!isConnected) return;
        sendPacket(new DisconnectC2SPacket());
        stop(true);
        output.disconnected();
    }

    public void sendPublicMessage(String message) {
        sendPacket(new PublicChatMessageC2SPacket(message));
    }

    public void sendPrivateMessage(User toUser, String message) {
        sendPacket(new PrivateChatMessageC2SPacket(toUser, message));
    }

}
