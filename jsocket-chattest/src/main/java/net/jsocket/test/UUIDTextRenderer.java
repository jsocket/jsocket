package net.jsocket.test;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class UUIDTextRenderer extends DefaultListCellRenderer {
    private final UUID thisID;

    public UUIDTextRenderer(UUID thisID) {
        this.thisID = thisID;
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
        if (thisID.equals(id)) ta.setBackground(new Color(220, 255, 220));
        if (isSelected) {
            ta.setBackground(new Color(180, 180, 255));
            ta.setFont(new Font(ta.getFont().getName(), Font.BOLD, ta.getFont().getSize()));
        }
        p.add(ta, BorderLayout.CENTER);
        int width = list.getWidth();
        // this is just to lure the ta's internal sizing mechanism into action
        if (width > 0) {
            ta.setSize(width, Short.MAX_VALUE);
        }
        return p;
    }
}
