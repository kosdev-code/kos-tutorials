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
    private static final int MAX_TEMP = 74;
    private static final int MIN_TEMP = 68;

    private int min_temp = MIN_TEMP;
    private int max_temp = MAX_TEMP;
    private Mode mode = Mode.HEAT;

    private final JLabel minValueLabel = new JLabel();
    private final JLabel maxValueLabel = new JLabel();
    private final JLabel currentValueLabel = new JLabel();
    private final JLabel modeLabel = new JLabel();

    public ThermostatUI() {
        super("Thermostat");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create a panel inside the frame for all the labels
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                setPointPanel(),        // set point panel on the left
                currentStatusPanel()    // current status (temp and mode) on the right
        );

        splitPane.setResizeWeight(0.66); // 70% left, 30% right
        splitPane.setDividerSize(0);    // hide divider
        splitPane.setEnabled(false);    // prevent dragging

        root.add(splitPane, BorderLayout.CENTER);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.66));
    }

    private JPanel currentStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 20, 0));

        configureModeLabel();
        configureCurrentTempLabel();

        panel.add(modeLabel);
        panel.add(currentValueLabel);
        return panel;
    }

    private JPanel setPointPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 20, 0));

        updateLabels();

        panel.add(createTempAdjustPanel(minValueLabel, Color.decode(Mode.COOL.getColor()),
                () -> {
                    min_temp++;
                    updateLabels();
                },
                () -> {
                    min_temp--;
                    updateLabels();
                }));
        panel.add(createTempAdjustPanel(maxValueLabel, Color.decode(Mode.HEAT.getColor()),
                () -> {
                    max_temp++;
                    updateLabels();
                },
                () -> {
                    max_temp--;
                    updateLabels();
                }));

        return panel;
    }

    private void configureModeLabel() {
        modeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        modeLabel.setOpaque(true);
        modeLabel.setFont(new Font("Arial", Font.BOLD, 20));
    }

    private void configureCurrentTempLabel() {
        currentValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentValueLabel.setFont(new Font("Arial", Font.BOLD, 36));

    }

    private void updateLabels() {
        minValueLabel.setText(String.valueOf(min_temp));
        maxValueLabel.setText(String.valueOf(max_temp));
        currentValueLabel.setText(CURRENT_TEMP + " F");
        modeLabel.setText(mode.name());
        modeLabel.setBackground(Color.decode(mode.getColor()));
    }

    private JPanel createTempAdjustPanel(JLabel label, Color background, Runnable onUp, Runnable onDown) {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 5)); // 5px vertical gap

        JButton upButton = createButton("up.png", onUp);
        JButton downButton = createButton("down.png", onDown);

        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(background);

        // Add components top-to-bottom: up button, temp label, down button
        panel.add(upButton);
        panel.add(label);
        panel.add(downButton);

        return panel;
    }

    private JButton createButton(String fileName, Runnable runnable) {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(fileName));
        JButton button = new JButton(icon);
        button.addActionListener(e -> runnable.run());
        button.setOpaque(false);
        return button;
    }
}
