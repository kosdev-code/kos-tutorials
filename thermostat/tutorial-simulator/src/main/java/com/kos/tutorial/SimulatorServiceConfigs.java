/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.service.config.ConfigBean;
import com.tccc.kos.commons.core.service.config.annotations.ConfigDesc;
import lombok.Getter;
import lombok.Setter;

/**
 * This is the configs associated with the thermostat simulator service
 *
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
@Getter @Setter
public class SimulatorServiceConfigs extends ConfigBean {
    @ConfigDesc(value = "Rate at which ambient temperature changes to set temperature when thermostat is on")
    private double temperatureChangeRate = 0.2;

    @ConfigDesc(value = "Rate at which ambient temperature changes to adjust the thermostat mode")
    private double ambientTemperatureRateChange = 0.05;

    @ConfigDesc(value = "Current operation mode of the thermostat")
    private int mode = 0;
}
