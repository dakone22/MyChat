package server;

import javax.swing.*;
import java.awt.*;

public class ServerWindow {
    static class ControlFrame extends JFrame {
        public JButton btnSendMessage;
        public JButton btnStart;
        public JTextField tfPort;
        public JTextField tfMessage;
        public JTextArea taLog;

        ControlFrame() {  // TODO: ServerHandler UI
            btnSendMessage = new JButton("Отправить");
            btnStart = new JButton("Запустить");
            tfPort = new JTextField("1234");
            tfPort.setMinimumSize(new Dimension(120, 20));
            tfMessage = new JTextField();
            taLog = new JTextArea();
            taLog.setEditable(false);
            taLog.setLineWrap(true);


            setBounds(600, 300, 600, 500);
            setTitle("ServerHandler");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            var jsp = new JScrollPane(taLog);
            this.add(jsp, BorderLayout.CENTER);

            var bottomPanel = new JPanel(new BorderLayout());
            this.add(bottomPanel, BorderLayout.SOUTH);
            bottomPanel.add(btnSendMessage, BorderLayout.EAST);
            bottomPanel.add(tfMessage, BorderLayout.CENTER);

            var eastPanel = new JPanel(new GridLayout(3, 1));
            this.add(eastPanel, BorderLayout.EAST);
            eastPanel.add(tfPort);
            eastPanel.add(btnStart);
        }
    }

    public ServerWindow() {
        final var window = new ControlFrame();
        final var app = new ServerApplication(getServerUserInterface(window));

        window.btnStart.addActionListener(e -> app.start(Integer.parseInt(window.tfPort.getText())));
        window.btnSendMessage.addActionListener(e -> app.send(window.tfMessage.getText()));

        window.setVisible(true);
    }

    private static ServerUserInterface getServerUserInterface(ControlFrame window) {
        return new ServerUserInterface() {
            @Override
            public void onServerStart() {
                window.taLog.append("[server] Start\n");
            }

            @Override
            public void onClientJoin(ClientConnectionHandler client) {
                window.taLog.append("[server] client %s joined!\n".formatted(client.toString()));
            }

            @Override
            public void onClientLeave(ClientConnectionHandler client) {
                window.taLog.append("[server] client %s left!\n".formatted(client.toString()));
            }

            @Override
            public void onPublicMessage(ClientConnectionHandler sender, String message) {
                window.taLog.append("[%s] %s\n".formatted(sender.toString(), message));
            }

            @Override
            public void onPrivateMessage(ClientConnectionHandler sender, String receiver, String message) {
                window.taLog.append("[%s -> %s] %s\n".formatted(sender.toString(), receiver, message));
            }

            @Override
            public void onExceptionOccurred(Object sender, Throwable exception) {
                window.taLog.append("[server] Error occurred: %s throws %s\n".formatted(sender.toString(), exception.getMessage()));
            }
        };
    }
}
