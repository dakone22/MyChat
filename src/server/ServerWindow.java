package server;

import client.gui.ChatLog;
import core.User;

import javax.swing.*;

public class ServerWindow extends JFrame {
    private JPanel pMain;
    private JTextPane jTextPane;
    public JButton btnStart;
    public JFormattedTextField tfPort;
    public JTextField tfMessage;
    public JButton btnSend;
    public JList<User> listUsers;
    private JPanel pConnectionFields;
    private JLabel lblPort;
    public JPasswordField passwordField;
    private JLabel lblPassword;
    private JPanel ChatPanel;
    private JPanel SidePanel;
    private JPanel SendPanel;

    public ChatLog log;

    public ServerWindow() {
        setBounds(600, 300, 800, 500);
        setTitle("ClientConnectionHandler");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(pMain);
        log = new ChatLog(jTextPane, user -> listUsers.setSelectedValue(user, true));
    }

}
