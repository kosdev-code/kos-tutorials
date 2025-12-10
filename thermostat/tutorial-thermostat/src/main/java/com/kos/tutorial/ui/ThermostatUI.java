/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial.ui;

import com.kos.tutorial.Mode;
import com.kos.tutorial.ThermostatService;
import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.util.ready.ReadyAndReadyListener;
import com.tccc.kos.commons.util.ready.ReadyIndicator;
import lombok.Getter;

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
public class ThermostatUI extends JFrame implements ReadyAndReadyListener {
    @Autowired
    private ThermostatService thermostatService;
    private final ReadyIndicator readyIndicator = new ReadyIndicator();

    // Dummy values to display
    private static final long CURRENT_TEMP = 70;

    private Mode mode = Mode.HEATING;

    private JPanel root;
    private ImageLabel currentTempLabel;
    private ImageLabel modeLabel;

    @Getter
    private JSplitPane splitPane;

    public ThermostatUI() {
        super("Thermostat");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create a panel inside the frame for all the labels
        root = new JPanel(new BorderLayout());
    }

    @Override
    public boolean onBeanReady() {
        splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                setPointPanel(),        // set point panel on the left
                currentStatusPanel()    // current status (temp and mode) on the right
        );

        splitPane.setDividerSize(0);    // hide divider
        splitPane.setEnabled(false);    // prevent dragging

        root.add(splitPane, BorderLayout.CENTER);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
        return true;
    }

    private JPanel currentStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 20, 0));

        modeLabel = new ImageLabel(
                new ImageIcon(getClass().getClassLoader().getResource("mode.png")),
                mode.name(),
                24
        );
        modeLabel.updateColor(Color.decode(mode.getColor()));

        currentTempLabel = new ImageLabel(
                new ImageIcon(getClass().getClassLoader().getResource("temperature.png")),
                CURRENT_TEMP + " F"
        );

        panel.add(modeLabel);
        panel.add(currentTempLabel);
        return panel;
    }

    private JPanel setPointPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 20, 0));

        ImageLabel minLabel = new ImageLabel(
                new ImageIcon(getClass().getClassLoader().getResource("minimum_temperature.png")),
                              String.valueOf(thermostatService.getMinTemp())
        );
        ImageLabel maxLabel = new ImageLabel(
                new ImageIcon(getClass().getClassLoader().getResource("maximum_temperature.png")),
                              String.valueOf(thermostatService.getMaxTemp())
        );

        panel.add(createTempAdjustPanel(minLabel,
                () -> {
                    thermostatService.setMinTemp(thermostatService.getMinTemp() + 1);;
                    minLabel.updateText(String.valueOf(thermostatService.getMinTemp()));
                },
                () -> {
                    thermostatService.setMinTemp(thermostatService.getMinTemp() - 1);;
                    minLabel.updateText(String.valueOf(thermostatService.getMinTemp()));
                }));
        panel.add(createTempAdjustPanel(maxLabel,
                () -> {
                    thermostatService.setMaxTemp(thermostatService.getMaxTemp() + 1);;
                    maxLabel.updateText(String.valueOf(thermostatService.getMaxTemp()));
                },
                () -> {
                    thermostatService.setMaxTemp(thermostatService.getMaxTemp() - 1);;
                    maxLabel.updateText(String.valueOf(thermostatService.getMaxTemp()));
                }));

        return panel;
    }

    private JPanel createTempAdjustPanel(ImageLabel label, Runnable onIncrease, Runnable onDecrease) {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 5)); // 5px vertical gap

        ImageButton increaseButton = new ImageButton(new ImageIcon(getClass().getClassLoader().getResource("increase_button.png")));
        ImageButton decreaseButton = new ImageButton(new ImageIcon(getClass().getClassLoader().getResource("decrease_button.png")));

        increaseButton.addActionListener(e -> onIncrease.run());
        decreaseButton.addActionListener(e -> onDecrease.run());

        // Add components top-to-bottom: up button, temp label, down button
        panel.add(increaseButton);
        panel.add(label);
        panel.add(decreaseButton);

        return panel;
    }

    /**
     * Updates the mode label with new mode and its associated color
     */
    public void updateMode(Mode newMode) {
        this.mode = newMode;
        modeLabel.updateText(mode.name());
        modeLabel.updateColor(Color.decode(mode.getColor()));
    }

    /**
     * Updates the current temperature display
     */
    public void updateCurrentTemp(long newTemp) {
        currentTempLabel.updateText(newTemp + " F");
    }

    @Override
    public ReadyIndicator getReady() {
        return readyIndicator;
    }
}
