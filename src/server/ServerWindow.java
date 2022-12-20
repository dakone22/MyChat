package server;

import client.gui.ChatLog;
import core.User;

import javax.swing.*;
import java.awt.*;

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
    public JButton btnStop;
    private JButton btnUnselect;
    public JButton btnKick;
    private JPanel connectControlPanel;
    private JPanel userListControl;

    public ChatLog log;

    public ServerWindow() {
        setBounds(600, 300, 800, 500);
        setTitle("Server");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
            btnKick.setEnabled(selected);
            btnSend.setText(selected ? "Send PM" : "Send");
        });
        btnUnselect.addActionListener(e -> listUsers.clearSelection());


        setState(State.Stopped);

        log = new ChatLog(jTextPane, user -> listUsers.setSelectedValue(user, true));
    }

    public enum State {
        Running, Stopped
    }

    public void setState(State state) {
        switch (state) {
            case Running -> setElementsState(true);
            case Stopped -> setElementsState(false);
        }
    }

    private void setElementsState(boolean isRunning) {
        btnStart.setEnabled(!isRunning);
        btnStart.setVisible(!isRunning);

        tfPort.setEnabled(!isRunning);
        passwordField.setEnabled(!isRunning);

        btnSend.setEnabled(isRunning);
        btnStop.setVisible(isRunning);
        btnStop.setEnabled(isRunning);
    }

}
