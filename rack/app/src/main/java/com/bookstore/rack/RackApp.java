package com.bookstore.rack;

import javax.swing.SwingUtilities;

import com.tccc.kos.commons.kab.KabFile;
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
    private static final String SECTION_ASSETS = "kos.assets";
    private static final String ASSETS_KAB_TYPE = "tutorial.assets";

    private KabFile assetsKab;

    @Override
    public void load() {
        assetsKab = getKabByType(SECTION_ASSETS, ASSETS_KAB_TYPE);
    }

    @Override
    public void started() throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RackUI(assetsKab).setVisible(true);
            }
        });
    }
}
