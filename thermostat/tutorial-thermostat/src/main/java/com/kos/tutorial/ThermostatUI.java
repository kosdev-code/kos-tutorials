/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import javax.swing.*;
import java.awt.*;

/**
 * Simple Java Swing UI for the thermostat
 * Displays the current temperature of the environment,
 * The set range of temperature by the user, and the current mode of the thermostat
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-10
 */
public class ThermostatUI extends JFrame {
    // Dummy values to display
    private static final long CURRENT_TEMP = 70;
    private static final long MAX_TEMP = 74;
    private static final long MIN_TEMP = 68;
    private static final Mode MODE = Mode.HEAT;

    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;

    public ThermostatUI() {
        super("Thermostat");

        // Window settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        // create a panel inside the frame for all the labels
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // create the label for the mode
        JPanel modeIndicator = new JPanel();
        modeIndicator.setBackground(Color.decode(MODE.getColor()));
        modeIndicator.setMaximumSize(new Dimension(WIDTH, 12));
        modeIndicator.setPreferredSize(new Dimension(0, 12));

        // create the label for the environment temp
        JLabel currentTempLabel = new JLabel(CURRENT_TEMP + " °F");
        currentTempLabel.setFont(new Font("Arial", Font.BOLD, 36));
        currentTempLabel.setAlignmentX(CENTER_ALIGNMENT);

        // create the label for the range of set temps
        JLabel rangeLabel = new JLabel("Min: " + MIN_TEMP + "   Max: " + MAX_TEMP);
        rangeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rangeLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Add all labels to the panel, and add the panel to the frame
        panel.add(modeIndicator);
        panel.add(Box.createVerticalStrut(15));
        panel.add(currentTempLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(rangeLabel);

        add(panel);

        // Popup the window
        setVisible(true);
    }
}
