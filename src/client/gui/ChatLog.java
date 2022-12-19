package client.gui;

import core.User;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class ChatLog {
    record UsernameLink(User user) {
    }

    public interface UserClickedListener {

        void onUserClick(User user);
    }

    private final StyledDocument doc;

    private final Style usernameStyle;
    private final Style messageStyle;
    private final Style publicMessageStyle;
    private final Style privateMessageStyle;
    private final Style systemMessageStyle;
    private final Style errorMessageStyle;

    public ChatLog(JTextPane jTextPane, UserClickedListener listener) {
        doc = jTextPane.getStyledDocument();

        jTextPane.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Element element = doc.getCharacterElement(jTextPane.viewToModel2D(e.getPoint()));
                AttributeSet as = element.getAttributes();
                UsernameLink fla = (UsernameLink) as.getAttribute("linkact");
                if (fla != null) {
                    listener.onUserClick(fla.user());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });


        usernameStyle = doc.addStyle("username", null);


        messageStyle = doc.addStyle("message", null);
        StyleConstants.setFontSize(messageStyle, 16);


        publicMessageStyle = doc.addStyle("public", messageStyle);

        privateMessageStyle = doc.addStyle("private", messageStyle);
        StyleConstants.setItalic(privateMessageStyle, true);

        systemMessageStyle = doc.addStyle("system", messageStyle);
        StyleConstants.setForeground(systemMessageStyle, Color.BLUE);

        errorMessageStyle = doc.addStyle("error", messageStyle);
        StyleConstants.setForeground(errorMessageStyle, Color.RED);
    }

    private void addStyledText(String text, Style style) {
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    private void addUsername(User user, Style parentStyle) {
        Style usernameStyle = doc.addStyle(user.username(), parentStyle);
        StyleConstants.setUnderline(usernameStyle, true);
        usernameStyle.addAttribute("linkact", new UsernameLink(user));
        addStyledText(user.username(), usernameStyle);
    }

    public void addPublicMessage(User user, String message) {
        addUsername(user, publicMessageStyle);
        addStyledText("> %s\n".formatted(message), publicMessageStyle);
    }

    public void addPrivateMessage(User fromUser, User toUser, String message) {
        addStyledText("[", privateMessageStyle);
        addUsername(fromUser, privateMessageStyle);
        addStyledText(" -> %s]> %s\n".formatted(toUser.username(), message), privateMessageStyle);
    }

    public void addSystemMessage(String message) {
        addStyledText("[SERVER]> %s\n".formatted(message), systemMessageStyle);
    }

    public void addErrorMessage(Throwable exception) {
        addStyledText("[ERROR] %s\n".formatted(exception.getMessage()), errorMessageStyle);
    }
}
