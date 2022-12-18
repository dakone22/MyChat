package client.gui;

import core.User;

import javax.swing.*;

public class ClientWindow extends JFrame {
    private JPanel pMain;
    private JTextPane jTextPane;
    public JButton btnConnect;
    public JFormattedTextField tfHost;
    public JFormattedTextField tfPort;
    public JTextField tfMessage;
    public JButton btnSend;
    public JList<User> listUsers;
    private JPanel pConnectionFields;
    private JLabel lblHost;
    private JLabel lblPort;
    public JPasswordField passwordField;
    private JLabel lblPassword;
    public JFormattedTextField tfUsername;
    private JLabel lblName;
    private JPanel ChatPanel;
    private JPanel SidePanel;
    private JPanel SendPanel;

    public ChatLog log;

    public ClientWindow() {
//            btnSendMessage = new JButton("Отправить");
//            btnStart = new JButton("Присоедениться");
//            tfHost = new JTextField("localhost");
//            tfPort = new JTextField("1234");
//            tfMessage = new JTextField();
//            taLog = new JTextArea();
//            taLog.setEditable(false);
//            taLog.setLineWrap(true);


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


        setBounds(600, 300, 800, 500);
        setTitle("ClientConnectionHandler");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(pMain);
//        pack();
//
//            var jsp = new JScrollPane(taLog);
//            this.add(jsp, BorderLayout.CENTER);
//
//            var bottomPanel = new JPanel(new BorderLayout());
//            this.add(bottomPanel, BorderLayout.SOUTH);
//            bottomPanel.add(btnSendMessage, BorderLayout.EAST);
//            bottomPanel.add(tfMessage, BorderLayout.CENTER);
//
//            var eastPanel = new JPanel(new GridLayout(3, 1));
//            this.add(eastPanel, BorderLayout.EAST);
//            eastPanel.add(tfHost);
//            eastPanel.add(tfPort);
//            eastPanel.add(btnStart);
        log = new ChatLog(jTextPane, user -> listUsers.setSelectedValue(user, true));

    }

}
