/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.core.dispatcher.annotations.ApiController;
import com.tccc.kos.commons.core.dispatcher.annotations.ApiEndpoint;

/**
 * Thermostat Service controller
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-10
 */
@ApiController(base = "/thermostat/service",
        title = "Thermostat Service controller",
        desc = "Endpoints for interacting with the thermostat")
public class ThermostatServiceController {
    @Autowired
    private ThermostatService service;

    @ApiEndpoint(GET = "/state",
            desc = "Get the state of the thermostat (temp, mode, color)")
    public ThermostatState getThermostatTemp () {
        return service.getState();
    }
}
