package net.jsocket.test;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class UUIDTextRenderer extends DefaultListCellRenderer {
    private final String username;

    public UUIDTextRenderer(String username) {
        this.username = username;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());

        UUID id = null;
        try {
            id = (UUID) value;
        } catch (ClassCastException e) {
            return p;
        }

        JTextArea ta = new JTextArea();
        ta.setLineWrap(true);
        ta.setText(id.toString());
        p.add(ta, BorderLayout.CENTER);
        int width = list.getWidth();
        // this is just to lure the ta's internal sizing mechanism into action
        if (width > 0) {
            ta.setSize(width, Short.MAX_VALUE);
        }
        return p;
    }
}
