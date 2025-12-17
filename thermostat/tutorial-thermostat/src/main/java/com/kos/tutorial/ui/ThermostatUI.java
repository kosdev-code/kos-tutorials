/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial.ui;

import com.kos.tutorial.Mode;
import com.kos.tutorial.ThermostatListener;
import com.kos.tutorial.ThermostatService;
import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.util.ready.ReadyAndReadyListener;
import com.tccc.kos.commons.util.ready.ReadyIndicator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class ThermostatUI extends JFrame implements ReadyAndReadyListener, ThermostatListener {
    private static final Mode INITIAL_MODE = Mode.OFF;
    private static final int INITIAL_TEMP = 70;

    @Autowired
    private ThermostatService thermostatService;
    private final ReadyIndicator readyIndicator = new ReadyIndicator();

    private final JPanel root;
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

    /**
     * This is called when all the dependencies are ready
     */
    @Override
    public boolean onBeanReady() {
        splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                setPointPanel(),        // set point panel on the left
                currentStatusPanel()    // current status (temp and mode) on the right
        );

        splitPane.setDividerSize(0);    // hide divider
        splitPane.setEnabled(false);    // prevent dragging
        splitPane.setResizeWeight(0.7);

        root.add(splitPane, BorderLayout.CENTER);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
        return true;
    }

    private JPanel currentStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        modeLabel = new ImageLabel(
                new ImageIcon(getClass().getClassLoader().getResource("mode.png")),
                INITIAL_MODE.name(),
                24
        );
        modeLabel.updateColor(Color.decode(INITIAL_MODE.getColor()));

        currentTempLabel = new ImageLabel(
                new ImageIcon(getClass().getClassLoader().getResource("temperature.png")), INITIAL_TEMP + " F"
        );

        panel.add(Box.createVerticalStrut(32)); // Add space at top
        panel.add(modeLabel);
        panel.add(Box.createVerticalStrut(40)); // Space between components
        panel.add(currentTempLabel);
        panel.add(Box.createVerticalGlue()); // Pushes everything up, expands at bottom

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

    @Override
    public ReadyIndicator getReady() {
        return readyIndicator;
    }

    /**
     * Callback when temperature is updated because ThermostatListener is implemented
     */
    @Override
    public void onTemperatureChange(double temperature) {
        currentTempLabel.updateText((int) temperature + " F");
    }

    /**
     * Callback when mode is updated because ThermostatListener is implemented
     */
    @Override
    public void onModeChange(Mode mode) {
        modeLabel.updateText(mode.name());
        modeLabel.updateColor(Color.decode(mode.getColor()));
    }
}
