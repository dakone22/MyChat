package client;

import core.User;
import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;
import core.network.packets.c2s.chat.ChatMessageC2SPacket;
import core.network.packets.c2s.chat.ChatUserListC2SPacket;
import core.network.packets.s2c.chat.*;
import core.network.packets.s2c.service.ConnectedFailureS2CPacket;
import core.network.packets.s2c.service.ConnectedSuccessS2CPacket;
import core.network.packets.s2c.service.DisconnectedS2CPacket;

import java.net.InetAddress;

public class ClientApplication {
    private final ClientApplicationListener output;
    private final ClientConnection networkHandler;

    private User currentUserData;

    public ClientApplication(ClientApplicationListener clientApplicationListener) {
        output = clientApplicationListener;

        networkHandler = new ClientConnection(new ClientConnectionListener() {
            @Override
            public void onPublicMessage(ChatMessageS2CPacket.Public packet) {
                // TODO: check sender
                output.onPublicMessage(packet.getIndex(), packet.getDateTime(), packet.getSender(), packet.getMessage());
            }

            @Override
            public void onPrivateMessage(ChatMessageS2CPacket.Private packet) {
                // TODO: check sender & receiver
                output.onPrivateMessage(packet.getIndex(), packet.getDateTime(), packet.getSender(), packet.getReceiver(), packet.getMessage());
            }

            @Override
            public void onSystemMessage(SystemMessageS2CPacket packet) {
                // TODO: SystemMessage
            }

            @Override
            public void onCustomSystemMessage(CustomSystemMessageS2CPacket packet) {
                output.onCustomMessage(packet.getIndex(), packet.getDateTime(), packet.getMessage());
            }

            @Override
            public void onClientJoin(ClientJoinS2CPacket packet) {
                output.onClientJoin(packet.getIndex(), packet.getDateTime(), packet.getUser());
                sendPacket(new ChatUserListC2SPacket());
            }

            @Override
            public void onClientLeave(ClientLeaveS2CPacket packet) {
                output.onClientLeave(packet.getIndex(), packet.getDateTime(), packet.getUser(), packet.getDisconnectReason());
                sendPacket(new ChatUserListC2SPacket());
            }

            @Override
            public void onConnected(ConnectedSuccessS2CPacket packet) {
                currentUserData = packet.getUser();
                output.onConnected(packet.getUser());
                sendPacket(new ChatUserListC2SPacket());
            }

            @Override
            public void onConnectFailed(ConnectedFailureS2CPacket packet) {

            }

            @Override
            public void onDisconnected(DisconnectedS2CPacket packet) {

            }
        });
        networkHandler.addExceptionOccurredListener(output);
    }

    private void sendPacket(Packet<? extends ServerPacketListener> packet) {
        try {
            networkHandler.sendPacket(packet);
        } catch (Exception e) {
            output.onExceptionOccurred(this, e);
        }
    }

    public void connect(String host, int port) {
        try {
            networkHandler.connect(InetAddress.getByName(host), port);
        } catch (Exception e) {
            output.onExceptionOccurred(this, e);
        }
    }

    public void sendPublicMessage(String text) {
        var messagePacket = new ChatMessageC2SPacket.Public(text);
        sendPacket(messagePacket);
    }

}
