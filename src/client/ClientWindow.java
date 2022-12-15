package client;

import core.User;
import core.network.packets.s2c.chat.ClientLeaveS2CPacket;
import core.network.packets.s2c.chat.SystemMessageS2CPacket;
import core.network.packets.s2c.service.ConnectedFailureS2CPacket;
import core.network.packets.s2c.service.DisconnectedS2CPacket;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class ClientWindow {
    static class ChatFrame extends JFrame {
        public JButton btnSendMessage;
        public JButton btnConnect;
        public JTextField tfHost;
        public JTextField tfPort;
        public JTextField tfMessage;
        public JTextArea taLog;

        ChatFrame() {  // TODO: ClientConnectionHandler UI
            btnSendMessage = new JButton("Отправить");
            btnConnect = new JButton("Присоедениться");
            tfHost = new JTextField("localhost");
            tfPort = new JTextField("1234");
            tfMessage = new JTextField();
            taLog = new JTextArea();
            taLog.setEditable(false);
            taLog.setLineWrap(true);


            // add UI elements to window
//            add(new JScrollPane(messageLog), BorderLayout.CENTER);
//            JPanel sendPanel = new JPanel();
//            sendPanel.add(messageField);
//            sendPanel.add(sendButton);
//            add(sendPanel, BorderLayout.SOUTH);
//            JPanel connectPanel = new JPanel();
//            connectPanel.add(new JLabel("Host:"));
//            connectPanel.add(hostField);
//            connectPanel.add(new JLabel("Port:"));
//            connectPanel.add(portField);
//            connectPanel.add(connectButton);
//            add(connectPanel, BorderLayout.EAST);


            setBounds(600, 300, 600, 500);
            setTitle("ClientConnectionHandler");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            var jsp = new JScrollPane(taLog);
            this.add(jsp, BorderLayout.CENTER);

            var bottomPanel = new JPanel(new BorderLayout());
            this.add(bottomPanel, BorderLayout.SOUTH);
            bottomPanel.add(btnSendMessage, BorderLayout.EAST);
            bottomPanel.add(tfMessage, BorderLayout.CENTER);

            var eastPanel = new JPanel(new GridLayout(3, 1));
            this.add(eastPanel, BorderLayout.EAST);
            eastPanel.add(tfHost);
            eastPanel.add(tfPort);
            eastPanel.add(btnConnect);
        }
    }

    public ClientWindow() {
        final var window = new ChatFrame();

        final ClientApplication app = new ClientApplication(getApplicationListener(window));

        window.btnConnect.addActionListener(e -> app.connect(window.tfHost.getText(), Integer.parseInt(window.tfPort.getText())));
        window.btnSendMessage.addActionListener(e -> app.sendPublicMessage(window.tfMessage.getText()));

        window.setVisible(true);
    }

    private static ClientApplicationListener getApplicationListener(ChatFrame window) {
        return new ClientApplicationListener() {
            @Override
            public void onCustomMessage(String message) {

            }

            @Override
            public void publicMessage(User sender, String message) {
                window.taLog.append("%s: %s\n".formatted(
                        sender.username(),
                        message
                ));
            }

            @Override
            public void privateMessage(User sender, User receiver, String message) {
                window.taLog.append("%s -> %s: %s\n".formatted(
                        sender,
                        receiver,
                        message
                ));
            }

            @Override
            public void clientJoined(User user) {
                window.taLog.append("[server] User %s joined\n".formatted(
                        user.username()
                ));
            }

            @Override
            public void clientLeft(User user, ClientLeaveS2CPacket.DisconnectReason reason) {
                window.taLog.append("[server] User %s left: %s\n".formatted(
                        user.username(),
                        reason.toString()
                ));
            }

            @Override
            public void connected(User assignedUser) {
                window.taLog.append("[server] Joined to server as %s\n".formatted(
                        assignedUser.username()
                ));
            }

            @Override
            public void updateUserList(List<User> userList) {
                // todo: userList
            }

            @Override
            public void disconnected(DisconnectedS2CPacket.DisconnectReason reason) {
                window.taLog.append("[server] Disconnected from server: %s\n".formatted(
                        reason.toString()
                ));
            }

            @Override
            public void systemMessage(SystemMessageS2CPacket.MessageType messageType) {
                window.taLog.append("[server] %s\n".formatted(
                        messageType.toString()
                ));
            }

            @Override
            public void connectFailed(ConnectedFailureS2CPacket.FailReason failReason) {
                window.taLog.append("[server] Connection Refused: %s\n".formatted(
                        failReason.toString()
                ));
            }

            @Override
            public void exceptionOccurred(Object sender, Throwable e) {
                window.taLog.append("[err] %s.\n".formatted(e.getMessage()));
            }
        };
    }
}
