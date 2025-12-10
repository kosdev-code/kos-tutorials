package com.kos.tutorial.ui;

import javax.swing.*;
import java.awt.*;

public class ImageButton extends JButton {

    public ImageButton(ImageIcon icon) {
        super(icon);
        setButtonStyle();
        this.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    }

    private void setButtonStyle() {
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
