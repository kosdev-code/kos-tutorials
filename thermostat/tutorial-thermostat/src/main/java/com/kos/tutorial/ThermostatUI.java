package com.kos.tutorial;

import javax.swing.*;
import java.awt.*;

public class ThermostatUI extends JFrame {

    private static final long CURRENT_TEMP = 70;
    private static final long MAX_TEMP = 74;
    private static final long MIN_TEMP = 68;
    private static final Mode MODE = Mode.HEAT;

    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    public ThermostatUI() {
        super("Thermostat");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel modeIndicator = new JPanel();
        modeIndicator.setBackground(Color.decode(MODE.getColor()));
        modeIndicator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
        modeIndicator.setPreferredSize(new Dimension(0, 12));

        JLabel currentTempLabel = new JLabel(CURRENT_TEMP + " °F");
        currentTempLabel.setFont(new Font("Arial", Font.BOLD, 36));
        currentTempLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel rangeLabel = new JLabel("Min: " + MIN_TEMP + "   Max: " + MAX_TEMP);
        rangeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rangeLabel.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(modeIndicator);
        panel.add(Box.createVerticalStrut(15));
        panel.add(currentTempLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(rangeLabel);

        add(panel);
    }
}
