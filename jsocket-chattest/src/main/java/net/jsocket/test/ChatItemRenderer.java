package net.jsocket.test;

import javax.swing.*;
import java.awt.*;

public class ChatItemRenderer extends DefaultListCellRenderer {
    private final String username;

    public ChatItemRenderer(String username) {
        this.username = username;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());

        ChatItem chatItem = null;
        try {
            chatItem = (ChatItem) value;
        } catch (ClassCastException e) {
            return p;
        }

        JTextArea sender = new JTextArea();
        sender.setLineWrap(true);
        sender.setFont(new Font(sender.getFont().getName(), Font.BOLD, sender.getFont().getSize()));
        JTextArea time = new JTextArea();
        time.setLineWrap(true);
        time.setFont(new Font(time.getFont().getName(), Font.ITALIC, time.getFont().getSize()));
        JTextArea content = new JTextArea();
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setText(chatItem.getText());
        long interval = 0;
        if (index > 0) {
            ChatItem previous = (ChatItem) list.getModel().getElementAt(index - 1);
            interval = (chatItem.getSent().getTime() - previous.getSent().getTime()) / 1000;
            if (!previous.getUsername().equals(chatItem.getUsername())) {
                sender.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
                sender.setText(chatItem.getUsername() + ":");
                time.setText(chatItem.getSent() + ":");
            } else if (interval > 5) {
                time.setText(chatItem.getSent() + ":");
            }
        } else {
            sender.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
            sender.setText(chatItem.getUsername() + ":");
            time.setText(chatItem.getSent() + ":");
        }

        if (index == 0 || interval > 5) p.add(sender, BorderLayout.NORTH);
        p.add(time, BorderLayout.CENTER);
        p.add(content, BorderLayout.SOUTH);
        if (username.equals(chatItem.getUsername())) {
            sender.setBackground(new Color(240, 240, 240));
            time.setBackground(new Color(240, 240, 240));
            content.setBackground(new Color(240, 240, 240));
        }
        int width = list.getWidth();
        // this is just to lure the ta's internal sizing mechanism into action
        if (width > 0) {
            sender.setSize(width, Short.MAX_VALUE);
            time.setSize(width, Short.MAX_VALUE);
            content.setSize(width, Short.MAX_VALUE);
        }
        return p;
    }
}
