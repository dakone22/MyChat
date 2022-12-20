package client.gui;

import core.User;

import javax.swing.*;
import java.awt.*;

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
    public JButton btnDisconnect;
    private JButton btnUnselect;
    private JPanel connectControlPanel;

    public ChatLog log;

    public ClientWindow() {
        setBounds(600, 300, 800, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setContentPane(pMain);
        listUsers.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                User user = (User) value;
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setText(user.username());
                return label;
            }
        });

        listUsers.addListSelectionListener(e -> {
            boolean selected = !listUsers.isSelectionEmpty();
            btnUnselect.setEnabled(selected);
            btnSend.setText(selected ? "Send PM" : "Send");
        });
        btnUnselect.addActionListener(e -> listUsers.clearSelection());

        setState(State.Disconnected);

        log = new ChatLog(jTextPane, user -> listUsers.setSelectedValue(user, true));


    }

    public enum State {
        Connected, Disconnected
    }

    public void setState(State state) {
        switch (state) {
            case Connected -> setElementsState(true);
            case Disconnected -> {
                setElementsState(false);
                ((DefaultListModel<User>) listUsers.getModel()).clear();
            }
        }
    }

    private void setElementsState(boolean isConnected) {
        btnConnect.setEnabled(!isConnected);
        btnConnect.setVisible(!isConnected);

        tfHost.setEnabled(!isConnected);
        tfPort.setEnabled(!isConnected);
        passwordField.setEnabled(!isConnected);
        tfUsername.setEnabled(!isConnected);

        tfMessage.setEnabled(isConnected);
        btnSend.setEnabled(isConnected);
        btnDisconnect.setVisible(isConnected);
        btnDisconnect.setEnabled(isConnected);
    }
}
