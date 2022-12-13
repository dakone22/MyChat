package server;

import core.events.server.ClientJoinEvent;
import core.events.ExceptionOccurredEvent;
import core.events.MessageReceivedEvent;
import core.events.server.ClientLeaveEvent;

import javax.swing.*;
import java.awt.*;

public class ServerUI {
    static class ServerWindow extends JFrame {
        public JButton btnSendMessage;
        public JButton btnStart;
        public JTextField tfPort;
        public JTextField tfMessage;
        public JTextArea taLog;

        ServerWindow() {  // TODO: Server UI
            btnSendMessage = new JButton("Отправить");
            btnStart = new JButton("Запустить");
            tfPort = new JTextField("1234");
            tfPort.setMinimumSize(new Dimension(120, 20));
            tfMessage = new JTextField();
            taLog = new JTextArea();
            taLog.setEditable(false);
            taLog.setLineWrap(true);


            setBounds(600, 300, 600, 500);
            setTitle("Server");
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

    public ServerUI() {
        final var window = new ServerWindow();

        final ServerApplication app = new ServerApplication(new ServerOutput() {
            @Override
            public void onNewMessage(MessageReceivedEvent event) {
                window.taLog.append("[%s] %s\n".formatted(event.sender.toString(), event.message));
            }

            @Override
            public void onExceptionOccurred(ExceptionOccurredEvent event) {
                window.taLog.append(event.exception.getMessage());
            }

            @Override
            public void onClientJoin(ClientJoinEvent event) {
                window.taLog.append("Client %s joined!\n".formatted(event.client.toString()));
            }

            @Override
            public void onClientLeaved(ClientLeaveEvent event) {
                window.taLog.append("Client %s leaved!\n".formatted(event.client.toString()));
            }

            @Override
            public void onServerStart() {
                window.taLog.append("Server started!\n");
            }
        });

        window.btnStart.addActionListener(e -> app.start(Integer.parseInt(window.tfPort.getText())));
        window.btnSendMessage.addActionListener(e -> app.send(window.tfMessage.getText()));

        window.setVisible(true);
    }
}
