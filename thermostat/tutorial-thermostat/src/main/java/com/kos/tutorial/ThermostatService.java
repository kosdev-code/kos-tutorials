/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.core.service.AbstractService;
import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.assembly.AssemblyAware;
import com.tccc.kos.core.service.assembly.AssemblyListener;
import com.tccc.kos.core.service.assembly.AssemblyService;

/**
 *
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-10
 */
public class ThermostatService extends AbstractService implements AssemblyListener {
    @Autowired
    private AssemblyService assemblyService;
    private ThermostatBoard thermostat;

    public ThermostatService() {}

    @Override
    public void onPostInstall(Assembly assembly) {
        if (assembly instanceof ThermostatAssembly trayAssembly) {
            thermostat = trayAssembly.getThermostat();
        }
    }

    public void setMaxTemp(long maxTemp) {}

    public void setMinTemp(long minTemp) {}

    public long getTemp() { return 0; }
}
