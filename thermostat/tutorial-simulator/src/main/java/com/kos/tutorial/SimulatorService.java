/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.service.AbstractConfigurableService;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgIdentity;
import com.tccc.kos.commons.util.concurrent.AdjustableCallback;

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
    private static final String NAME = "kos.tutorial.simulator";
    private static final String IDENTITY = "test";

    private final Thermostat thermostat;
    private Random random;
    private ThermostatClient client;

    public SimulatorService() {
        thermostat  = new Thermostat();
        random = new Random();
    }

    @Override
    public boolean onBeanReady() {
        // Timer to change simulate slight shifts in the environment temperature
        AdjustableCallback timer = new AdjustableCallback(true, getConfig().getAmbientTemperatureChangeDelay(), this::changeTemp);
        connectToNetwork();
        return true;
    }

    /**
     * Simulates small shifts in the environment temperature
     */
    private void changeTemp() {
        if (thermostat.getMode() == Mode.OFF.ordinal()) {
            thermostat.setTemp(thermostat.getTemp() + random.nextGaussian() * getConfig().getAmbientTempStd());
        } else if (thermostat.getMode() == Mode.HEATING.ordinal()) {
            thermostat.setTemp(thermostat.getTemp() + getConfig().getTemperatureChangeRate());
        } else {
            thermostat.setTemp(thermostat.getTemp() - getConfig().getTemperatureChangeRate());
        }
    }

    public void connectToNetwork() {
        BinaryMsgIdentity identity = new BinaryMsgIdentity();
        identity.setName(NAME);
        identity.setNodeName(IDENTITY);
        identity.setNodeType(IDENTITY);
        // binary message client identity
        client = new ThermostatClient(thermostat, identity);
        client.start();
    }

    public void disconnectFromNetwork() {
        if (client != null) {
            client.stop();
        }
    }
}
