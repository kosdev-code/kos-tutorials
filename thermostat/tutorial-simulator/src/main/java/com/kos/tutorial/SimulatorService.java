/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.service.AbstractConfigurableService;
import com.tccc.kos.commons.util.concurrent.AdjustableCallback;
import lombok.Getter;

import java.util.Random;

/**
 * This is the service that coordinates simulation of the thermostat
 * The service provides the temperature reading from the thermostat, and the mode
 * The mode can also be set externally
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
public class SimulatorService extends AbstractConfigurableService<SimulatorServiceConfigs> {
    @Getter
    private double ambientTemperature = 70;
    private Random random;

    public SimulatorService() {
        random = new Random();
        // Timer to change simulate slight shifts in the ambient temperature
        AdjustableCallback timer = new AdjustableCallback(true, 1000, this::changeAmbientTemp);
    }

    /**
     * Simulates small shifts in the ambient temperature
     */
    private void changeAmbientTemp() {
        ambientTemperature += random.nextGaussian();;
    }

    public int getMode() {
        return getConfig().getMode();
    }

    public void setMode(int mode) {
        getConfig().setMode(mode);
    }
}
