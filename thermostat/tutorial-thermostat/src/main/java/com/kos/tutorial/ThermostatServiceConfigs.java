/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.service.config.ConfigBean;
import com.tccc.kos.commons.core.service.config.annotations.ConfigDesc;
import lombok.Getter;
import lombok.Setter;

/**
 * These are the configs linked to Thermostat Service
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-10
 */
@Getter @Setter
public class ThermostatServiceConfigs extends ConfigBean {
    @ConfigDesc(value = "Maximum temperature set on the thermostat")
    private int maxTemp = 74;

    @ConfigDesc(value = "Minimum temperature set on the thermostat")
    private int minTemp = 68;
}
