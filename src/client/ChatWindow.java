package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatWindow {
    private JPanel pMain;
    private JTextPane log;
    private JButton btnConnect;
    private JFormattedTextField formattedTextField1;
    private JFormattedTextField formattedTextField2;
    private JTextField tfMessage;
    private JButton btnSend;
    private JList listUsers;
    private JPanel pConnectionFields;

    public ChatWindow() {
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
}
