package client;

import client.gui.ClientWindow;
import core.User;
import core.network.packets.s2c.chat.ClientLeaveS2CPacket;
import core.network.packets.s2c.chat.SystemMessageS2CPacket;
import core.network.packets.s2c.service.ConnectedFailureS2CPacket;
import core.network.packets.s2c.service.DisconnectedS2CPacket;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class ClientUI {
    private final ClientWindow window;
    private final ClientController app;

    public ClientUI() {
        window = new ClientWindow();

        app = new ClientController(new ClientUIUpdater() {
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
            public void clientLeft(User user, ClientLeaveS2CPacket.LeaveReason reason) {
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
                window.setState(ClientWindow.State.Connected);
            }

            @Override
            public void forceDisconnected(DisconnectedS2CPacket.DisconnectReason reason) {
                window.log.addSystemMessage("Disconnected from server: %s".formatted(
                        reason.toString()
                ));
                window.setState(ClientWindow.State.Disconnected);
            }

            @Override
            public void connectFailed(ConnectedFailureS2CPacket.FailReason failReason) {
                window.log.addSystemMessage("Connection Refused: %s".formatted(failReason.toString()));
                window.setState(ClientWindow.State.Disconnected);
            }

            @Override
            public void disconnected() {
                window.log.addSystemMessage("Disconnected");
                window.setState(ClientWindow.State.Disconnected);
            }

            @Override
            public void updateUserList(Iterable<User> userList) {
                DefaultListModel<User> listModel = (DefaultListModel<User>) window.listUsers.getModel();
                listModel.removeAllElements();

                for (var user : userList) {
                    listModel.addElement(user);
                }
            }

            @Override
            public void systemMessage(SystemMessageS2CPacket.MessageType messageType) {
                window.log.addSystemMessage(messageType.toString());
            }

            @Override
            public void exceptionOccurred(Object source, Throwable e) {
                window.log.addErrorMessage(e);
                window.setState(ClientWindow.State.Disconnected);
            }
        });

        window.btnConnect.addActionListener(e -> app.connect(
                window.tfHost.getText(),
                Integer.parseInt(window.tfPort.getText()),
                new String(window.passwordField.getPassword()),
                window.tfUsername.getText()
        ));

        window.btnSend.addActionListener(e -> {
            if (window.tfMessage.getText().isEmpty()) return;;
            if (window.listUsers.isSelectionEmpty()) {
                app.sendPublicMessage(window.tfMessage.getText());
            } else {
                app.sendPrivateMessage(window.listUsers.getSelectedValue(), window.tfMessage.getText());
            }
            window.tfMessage.setText("");
        });

        window.btnDisconnect.addActionListener(e -> app.softDisconnect());

        window.tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) window.btnSend.doClick();
            }
        });

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.softDisconnect();
                System.exit(0);
            }
        });


        window.setVisible(true);
    }
}
