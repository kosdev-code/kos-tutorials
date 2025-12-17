/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.assembly.CoreAssembly;
import lombok.Getter;

/**
 * This is the assembly for the thermostat
 * An assembly is a collection of boards, in this case the assembly contains the
 * <code>ThermostatBoard</code>
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
public class ThermostatAssembly extends Assembly implements CoreAssembly {
    @Getter
    private ThermostatBoard thermostat;

    // Create a constructor that follows the super Assembly
    public ThermostatAssembly(String name) {
        super(name);
    }

    @Override
    public void load() {
        // Load all boards in the assembly here.
        // In this tutorial we only have the thermostat board
        thermostat = new ThermostatBoard(this, "thermostat");
    }

    @Override
    public void start() {}
}
