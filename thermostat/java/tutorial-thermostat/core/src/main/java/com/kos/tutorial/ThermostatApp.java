/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.core.vfs.VFSSource;
import com.tccc.kos.commons.kab.KabFile;
import com.tccc.kos.core.service.app.BaseAppConfig;
import com.tccc.kos.core.service.app.SystemApplication;
import com.tccc.kos.core.service.browser.BrowserService;
import lombok.extern.slf4j.Slf4j;

/**
 * System application for a thermostat that connects to a physical thermostat
 * It can read temperature, set range of temp, and set the mode to heat or cool accordingly
 * It also has a display
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
@Slf4j
public class ThermostatApp extends SystemApplication<BaseAppConfig> {
    public static final String KAB_TYPE_UI = "kos.ui";

    @Autowired
    private BrowserService browserService;
    private VFSSource uiVfsSource;

    @Override
    public void load() {
        // Load the UI KAB
        KabFile uiKab = getKabByType(KAB_TYPE_UI);
        if (uiKab != null) {
            uiVfsSource = getVfs().mount("/ui", uiKab);
        } else {
            log.error("kos.ui KAB not found.");
        }

        // Beans added to the context in load() are automatically autowired before the start() callback
        addToCtx(new ThermostatService());
    }

    @Override
    public void start() {
        // install the core assembly: this assembly loads boards
        installAssembly(new ThermostatAssembly("core"));
    }

    @Override
    public void started() {
        // Browser-based UI
        browserService.goToUrl(uiVfsSource.getFullPath("index.html"));
    }
}