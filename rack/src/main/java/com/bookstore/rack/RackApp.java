// @kdoc-sysapp@
/**
 * (C) Copyright 2024, TCCC, All rights reserved.
 */
package com.bookstore.rack;

import javax.swing.SwingUtilities;

import com.tccc.kos.core.service.app.BaseAppConfig;
import com.tccc.kos.core.service.app.SystemApplication;

/**
 * System application for simple digital rack that has a display that
 * rotates content.
 *
 * @author David Vogt (david@kondra.com)
 * @version 2024-09-17
 */
public class RackApp extends SystemApplication<BaseAppConfig> {
    @Override
    public void load() throws Exception {
    }

    @Override
    public void start() throws Exception {
    }

    @Override
    public void started() throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RackUI().setVisible(true);
            }
        });
    }
    /**
     * Intellij requires a main() for the debugger to work.
     */
    public static void main(String[] args) {
    }
}

// @kdoc-sysapp@
