/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.broker.MessageBroker;
import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.core.service.AbstractConfigurableService;
import com.tccc.kos.commons.core.service.config.BeanChanges;
import com.tccc.kos.commons.util.ListenerList;
import com.tccc.kos.commons.util.concurrent.AdjustableCallback;
import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.assembly.AssemblyPrePostInstallListener;


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
public class ThermostatService extends AbstractConfigurableService<ThermostatServiceConfig> implements AssemblyPrePostInstallListener {
    private static final String TOPIC_THERMOSTAT_STATE = "/thermostat/state";

    @Autowired
    private MessageBroker broker;
    @Autowired
    private final ListenerList<ThermostatListener> listeners = new ListenerList<>();
    private ControlBoard controlBoard;
    private final AdjustableCallback timer;

    public ThermostatService() {
        // Create a recurring timer that fires every 1000 ms (1 second)
        // Each time it fires, re-evaluate the thermostat state
        timer = new AdjustableCallback(true, 1000, this::pollHardware);
    }

    /**
     * Called after an Assembly is installed.
     * <p>
     * The service listens for the ThermostatAssembly, so it can safely
     * retrieve the ThermostatBoard at the correct point in the lifecycle.
     * This avoids race conditions and manual wiring.
     */
    @Override
    public void onPostInstall(Assembly assembly) {
        if (assembly instanceof ThermostatAssembly trayAssembly) {
            controlBoard = trayAssembly.getControlBoard();

            // Safely start the timer
            timer.start();
        }
    }

    /**
     * Any time a config is changed (example: temperature setpoints) it will trigger
     * the pollHardware() method so that any changes like operation mode can be made
     */
    @Override
    public void onConfigChanged(BeanChanges changes) {
        // This callback may happen before the onPostInstall() callback, so do a null check
        if (controlBoard != null) {
            pollHardware();
        }
    }

    public int getMinTemp() {
        return getConfig().getMinTemp();
    }

    public int getMaxTemp() {
        return getConfig().getMaxTemp();
    }

    /**
     * Update the maximum temperature set point.
     * This value is stored in persistent config.
     */
    public void setMaxTemp(int maxTemp) {
        getConfig().setMaxTemp(maxTemp);
    }

    /**
     * Update the minimum temperature set point.
     * This value is stored in persistent config.
     */
    public void setMinTemp(int minTemp) {
        getConfig().setMinTemp(minTemp);
    }

    /**
     * Read the current environment temperature from the board.
     */
    public Integer getTemp() {
        return controlBoard.getTemp();
    }

    /**
     * Send the desired operating mode to the thermostat hardware.
     * This call is forwarded to the logical board, which will eventually
     * communicate with real or simulated physical hardware.
     */
    public void setMode(Mode mode) {
        controlBoard.setMode(mode);
    }

    /**
     * Check the environment temperature against the configured min/max
     * and update the board mode accordingly
     */
    private void pollHardware() {
        Integer temp = getTemp();

        // This is a null check in case the ifaceClient returns null
        if (temp == null) return;

        // Determine the mode
        Mode mode = (temp < getConfig().getMinTemp()) ? Mode.HEATING :
                    (temp > getConfig().getMaxTemp()) ? Mode.COOLING :
                    Mode.OFF;

        // Update physical board state
        setMode(mode);

        // Send temp and mode values to UI
        broker.send(TOPIC_THERMOSTAT_STATE, new ThermostatState(temp, mode.name()));

        // Notify listeners
        listeners.forEach(l -> l.onTemperatureChange(temp));
        listeners.forEach(l -> l.onModeChange(mode));
    }

    public ThermostatState getState() {
        Mode mode = controlBoard.getMode();
        return new ThermostatState(getTemp(), mode.name());
    }
}
