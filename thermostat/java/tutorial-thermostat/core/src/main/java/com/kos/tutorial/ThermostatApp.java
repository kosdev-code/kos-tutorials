/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import javax.swing.*;

import com.kos.tutorial.ui.ThermostatUI;
import com.tccc.kos.core.service.app.BaseAppConfig;
import com.tccc.kos.core.service.app.SystemApplication;

/**
 * System application for a thermostat that connects to a physical thermostat
 * It can read temperature, set range of temp, and set the mode to heat or cool accordingly
 * It also has a display
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
public class ThermostatApp extends SystemApplication<BaseAppConfig> {

    @Override
    public void load() {
        // Beans added to the context in load() are automatically autowired before the start() callback
        addToCtx(new ThermostatService());
        addToCtx(new ThermostatServiceController());
    }

    @Override
    public void start() {
        // install the core assembly
        // This assembly loads boards
        installAssembly(new ThermostatAssembly("core"));
    }

    @Override
    public void started() {
        // Create the java swing UI window
        SwingUtilities.invokeLater(() -> {
            ThermostatUI ui = new ThermostatUI();
            addToCtx(ui);

            // Beans added after load() must be explicitly autowired
            getCtx().update();

            ui.setVisible(true);
        });
    }
}