package client;

import core.network.packets.s2c.*;

import javax.swing.*;
import java.awt.*;


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

        final ClientApplication app = new ClientApplication(new ClientClientInterface() {
            @Override
            public void onPublicMessage(ChatMessageS2CPacket.Public packet) {
                window.taLog.append("[%s] (%d) %s: %s\n".formatted(
                        packet.getDateTime().toString(),
                        packet.getIndex(),
                        packet.getSender(),
                        packet.getMessage()
                ));
            }

            @Override
            public void onPrivateMessage(ChatMessageS2CPacket.Private packet) {
                window.taLog.append("[%s] (%d) %s -> %s: %s\n".formatted(
                        packet.getDateTime().toString(),
                        packet.getIndex(),
                        packet.getSender(),
                        packet.getReceiver(),
                        packet.getMessage()
                ));
            }

            @Override
            public void onSystemMessage(SystemMessageS2CPacket packet) {
                // TODO: system messages
            }

            @Override
            public void onCustomSystemMessage(CustomSystemMessageS2CPacket packet) {
                window.taLog.append("[%s] (%d) SERVER: %s\n".formatted(
                        packet.getDateTime().toString(),
                        packet.getIndex(),
                        packet.getMessage()
                ));
            }

            @Override
            public void onClientJoin(ClientJoinS2CPacket packet) {
                window.taLog.append("[%s] (%d) %s joined!\n".formatted(
                        packet.getDateTime().toString(),
                        packet.getIndex(),
                        packet.getClient()
                ));
            }

            @Override
            public void onClientLeave(ClientLeaveS2CPacket packet) {
                window.taLog.append("[%s] (%d) %s left (%s)\n".formatted(
                        packet.getDateTime().toString(),
                        packet.getIndex(),
                        packet.getClient(),
                        packet.getDisconnectReason().toString()
                ));
            }

            @Override
            public void onExceptionOccurred(Object sender, Throwable e) {
                window.taLog.append("[err] %s.\n".formatted(e.getMessage()));
            }
        });

        window.btnConnect.addActionListener(e -> app.connect(window.tfHost.getText(), Integer.parseInt(window.tfPort.getText())));
        window.btnSendMessage.addActionListener(e -> app.sendPublicMessage(window.tfMessage.getText()));

        window.setVisible(true);
    }
}
