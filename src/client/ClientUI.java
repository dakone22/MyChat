package client;

import client.gui.ClientWindow;
import core.User;
import core.network.packets.s2c.chat.ClientLeaveS2CPacket;
import core.network.packets.s2c.chat.SystemMessageS2CPacket;
import core.network.packets.s2c.service.ConnectedFailureS2CPacket;
import core.network.packets.s2c.service.DisconnectedS2CPacket;

import javax.swing.*;


public class ClientUI {
    private final ClientWindow window;

    public ClientUI() {
        window = new ClientWindow();

        final var app = new ClientController(getApplicationListener());

        window.btnConnect.addActionListener(e -> app.connect(
                window.tfHost.getText(),
                Integer.parseInt(window.tfPort.getText()),
                new String(window.passwordField.getPassword()),
                window.tfUsername.getText()));
        window.btnSend.addActionListener(e -> app.sendPublicMessage(window.tfMessage.getText()));

        window.setVisible(true);
    }

    private ClientUIUpdater getApplicationListener() {
        return new ClientUIUpdater() {
            @Override
            public void onCustomMessage(String message) {
                window.log.addSystemMessage(message);
            }

            @Override
            public void publicMessage(User sender, String message) {
                window.log.addPublicMessage(sender, message);
            }

            @Override
            public void privateMessage(User sender, User receiver, String message) {
                window.log.addPrivateMessage(sender, receiver, message);

            }

            @Override
            public void clientJoined(User user) {
                window.log.addSystemMessage("User %s joined".formatted(user.username()));
            }

            @Override
            public void clientLeft(User user, ClientLeaveS2CPacket.DisconnectReason reason) {
                window.log.addSystemMessage("User %s left: %s\n".formatted(
                        user.username(),
                        reason.toString()
                ));
            }

            @Override
            public void connected(User assignedUser) {
                window.log.addSystemMessage("Joined to server as %s".formatted(
                        assignedUser.username()
                ));
            }

            @Override
            public void updateUserList(Iterable<User> userList) {
                DefaultListModel<User> listModel = (DefaultListModel<User>) window.listUsers.getModel();
                listModel.removeAllElements();

                for (var user : userList) { listModel.addElement(user); }
            }

            @Override
            public void disconnected(DisconnectedS2CPacket.DisconnectReason reason) {
                window.log.addSystemMessage("Disconnected from server: %s".formatted(
                        reason.toString()
                ));
            }

            @Override
            public void systemMessage(SystemMessageS2CPacket.MessageType messageType) {
                window.log.addSystemMessage(messageType.toString());
            }

            @Override
            public void connectFailed(ConnectedFailureS2CPacket.FailReason failReason) {
                window.log.addSystemMessage("Connection Refused: %s".formatted(failReason.toString()));
            }

            @Override
            public void exceptionOccurred(Object sender, Throwable e) {
                window.log.addErrorMessage(e);
            }
        };
    }
}
