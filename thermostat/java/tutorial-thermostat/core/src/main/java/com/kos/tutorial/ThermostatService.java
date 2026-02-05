/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.broker.MessageBroker;
import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.core.service.AbstractConfigurableService;
import com.tccc.kos.commons.core.service.config.BeanChanges;
import com.tccc.kos.commons.util.concurrent.AdjustableCallback;

/**
 * ThermostatService is the central coordinator for thermostat behavior.
 * <p>
 * This service:
 *  - Reads the environment temperature from the ThermostatBoard
 *  - Compares it against user-defined temperature set points (configs)
 *  - Determines the correct operating mode (HEAT, COOL, OFF)
 *  - Sends that mode back to the board
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
public class ThermostatService extends AbstractConfigurableService<ThermostatServiceConfig> {
    private static final String TOPIC_THERMOSTAT_STATE = "/thermostat/state";

    @Autowired
    private MessageBroker broker;
    private ControlBoard controlBoard;
    private final AdjustableCallback timer;

    public ThermostatService() {
        // Create a recurring timer that fires every 1000 ms (1 second)
        // Each time it fires, re-evaluate the thermostat state
        timer = new AdjustableCallback(true, 1000, this::pollHardware);
    }

    /**
     * Initializes the service with a control board and starts the periodic hardware
     * polling timer.
     */
    public void start(ControlBoard controlBoard) {
        this.controlBoard = controlBoard;
        timer.start();
    }

    /**
     * Any time a config is changed (example: temperature setpoints) it will trigger
     * the pollHardware() method so that any changes like operation mode can be made
     */
    @Override
    public void onConfigChanged(BeanChanges changes) {
        // This callback may happen before the board has been initialized, so do a null check
        if (controlBoard != null) {
            pollHardware();
        }
    }

    /**
     * Check the environment temperature against the configured min/max
     * and update the board mode accordingly
     */
    private void pollHardware() {
        Integer temp = controlBoard.getTemp();
        if (temp == null) return;

        // Evaluate to determine the mode
        Mode mode = (temp < getConfig().getMinTemp()) ? Mode.HEATING :
                    (temp > getConfig().getMaxTemp()) ? Mode.COOLING :
                    Mode.OFF;

        // Update physical board state
        controlBoard.setMode(mode);

        // Send temp and mode values to UI
        broker.send(TOPIC_THERMOSTAT_STATE, new ThermostatState(temp, mode.name()));
    }
}
