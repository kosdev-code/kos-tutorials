/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

/**
 * This is the listener interface for getting callbacks about changes in the thermostat
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
public interface ThermostatListener {

    void onTemperatureChange(int temperature);
    void onModeChange(Mode mode);
}
