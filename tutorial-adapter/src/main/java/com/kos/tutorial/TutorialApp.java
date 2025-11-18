package com.kos.tutorial;

import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.kab.KabFile;
import com.tccc.kos.core.service.app.BaseAppConfig;
import com.tccc.kos.core.service.app.SystemApplication;
import com.tccc.kos.core.service.fuse.FuseMount;
import com.tccc.kos.core.service.fuse.FuseService;

public class TutorialApp extends SystemApplication<BaseAppConfig> {

    @Autowired
    private FuseService fuseService;

    @Override
    public void load() throws Exception {
        KabFile adapter = getKabByType("kos.adapter");
        if (adapter != null) {
            FuseMount mount = fuseService.mount(adapter);
            addToCtx(new ArduinoAdapterFactory(mount.getRootDir()));
        }

    }

    @Override
    public void start() throws Exception {
        installAssembly(new TutorialAssembly());
    }

}
