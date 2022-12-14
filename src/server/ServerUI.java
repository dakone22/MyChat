package server;

import core.User;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ServerUI {
    private final ServerWindow window;
    private final ServerController app;

    public ServerUI() {
        window = new ServerWindow();
        app = new ServerController(new ServerUIUpdater() {
            @Override
            public void onServerStart() {
                window.log.addSystemMessage("Start");
                window.setState(ServerWindow.State.Running);
            }

            @Override
            public void onServerStop() {
                window.log.addSystemMessage("Stop");
                window.setState(ServerWindow.State.Stopped);
            }

            @Override
            public void onClientJoined(User user) {
                window.log.addSystemMessage("joined");
            }

            @Override
            public void onClientLeft(User user) {
                window.log.addSystemMessage("left");
            }

            @Override
            public void onPublicMessage(User sender, String message) {
                window.log.addPublicMessage(sender, message);
            }

            @Override
            public void onPrivateMessage(User sender, User receiver, String message) {
                window.log.addPrivateMessage(sender, receiver, message);
            }

            @Override
            public void updateUserList(Iterable<User> users) {
                DefaultListModel<User> listModel = (DefaultListModel<User>) window.listUsers.getModel();
                listModel.removeAllElements();

                for (var user : users) listModel.addElement(user);
            }

            @Override
            public void onAuthorizationTimeout(ClientConnectionHandler client) {
                window.log.addSystemMessage("Client authorization timeout: %s".formatted(client.toString()));
            }

            @Override
            public void onAuthorizationFailed(ClientConnectionHandler client) {
                window.log.addSystemMessage("Client authorization failed: %s".formatted(client.toString()));
            }

            @Override
            public void onCustomMessage(String msg) {
                window.log.addSystemMessage(msg);
            }

            @Override
            public void onClientExceptionDisconnected(User user, Throwable exception) {
                window.log.addSystemMessage("Client \"%s\" forceDisconnected: %s".formatted(user.username(), exception.getMessage()));
            }

            @Override
            public void exceptionOccurred(Object source, Throwable exception) {
                window.log.addErrorMessage(exception);
            }
        });

        window.btnStart.addActionListener(e -> app.start(Integer.parseInt(window.tfPort.getText()), new String(window.passwordField.getPassword())));

        window.btnSend.addActionListener(e -> {
            String msg = window.tfMessage.getText();
            if (window.listUsers.isSelectionEmpty()) {
                app.sendCustomMessage(msg);
            } else {
                app.sendCustomMessage(window.listUsers.getSelectedValue(), msg);
            }
            window.tfMessage.setText("");
        });

        window.btnKick.addActionListener(e -> app.kickUser(window.listUsers.getSelectedValue()));

        window.btnStop.addActionListener(e -> app.stop());

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.stop();
                System.exit(0);
            }
        });

        window.tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) window.btnSend.doClick();
            }
        });



        window.setVisible(true);
    }

}
