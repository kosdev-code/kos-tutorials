package com.kos.tutorial.ui;

import javax.swing.*;
import java.awt.*;

public class ImageLabel extends JLabel {
    private final JLabel textLabel = new JLabel();

    public ImageLabel(ImageIcon icon, String text) {
        this(icon, text, 40); // default to size 40
    }

    public ImageLabel(ImageIcon icon, String text, int fontSize) {
        super(icon);
        this.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        this.setLayout(new BorderLayout());
        setTextLabel(fontSize);
        textLabel.setText(text);
    }

    public void updateText(String text) {
        textLabel.setText(text);
    }

    public void updateColor(Color color) {
        textLabel.setForeground(color);
    }

    private void setTextLabel(int fontSize) {
        textLabel.setOpaque(false);
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setVerticalAlignment(SwingConstants.CENTER);
        textLabel.setFont(new Font("Montserrat", Font.BOLD, fontSize));
        this.add(textLabel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
