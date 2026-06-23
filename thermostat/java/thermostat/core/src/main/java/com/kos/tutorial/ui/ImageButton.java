/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial.ui;

import javax.swing.*;
import java.awt.*;

/**
 * A java swing button with an image as the backgroun
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
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
