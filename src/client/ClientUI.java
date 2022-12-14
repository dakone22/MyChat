package client;

import core.events.ExceptionOccurredEvent;
import core.events.MessageReceivedEvent;

import javax.swing.*;
import java.awt.*;


public class ClientUI {
    static class ClientWindow extends JFrame {
        public JButton btnSendMessage;
        public JButton btnConnect;
        public JTextField tfHost;
        public JTextField tfPort;
        public JTextField tfMessage;
        public JTextArea taLog;

        ClientWindow() {  // TODO: Client UI
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
            setTitle("Client");
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

    public ClientUI() {
        final var window = new ClientWindow();

        final ClientApplication app = new ClientApplication(new ClientOutput() {
            @Override
            public void newMessage(MessageReceivedEvent event) {
                window.taLog.append("[msg] %s\n".formatted(event.message));
            }

            @Override
            public void exceptionOccurred(ExceptionOccurredEvent event) {
                window.taLog.append("[err] %s.\n".formatted(event.exception.getMessage()));
            }
        });

        window.btnConnect.addActionListener(e -> app.connect(window.tfHost.getText(), Integer.parseInt(window.tfPort.getText())));
        window.btnSendMessage.addActionListener(e -> app.send(window.tfMessage.getText()));

        window.setVisible(true);
    }
}
