package com.kos.tutorial;

import com.kosdev.kos.commons.core.context.annotations.Autowired;
import com.kosdev.kos.commons.kab.KabFile;
import com.kosdev.kos.core.service.app.BaseAppConfig;
import com.kosdev.kos.core.service.app.SystemApplication;
import com.kosdev.kos.core.service.fuse.FuseMount;
import com.kosdev.kos.core.service.fuse.FuseService;
import com.kosdev.kos.core.service.spawn.SpawnService;

import lombok.extern.slf4j.Slf4j;

// extract-code adapter-s12
// extract-code adapter-s4
// extract-code adapter-s1
@Slf4j
public class TutorialApp extends SystemApplication<BaseAppConfig> {

    // extract-code ignore start adapter-s4
    // extract-code ignore adapter-s1
    @Autowired
    private FuseService fuseService;

    // extract-code ignore end adapter-s4
    @Override
    public void load() throws Exception {
        // extract-code ignore start adapter-s1
        KabFile adapter = getKabByType("kos.adapter");
        if (adapter != null) {
            FuseMount mount = fuseService.mount(adapter);
            addToCtx(new ArduinoAdapterFactory(mount.getRootDir()));
        } else {
            log.error("No adapter found");
        }
        // extract-code ignore end adapter-s1
    }

    // extract-code ignore adapter-s12
    @Override
    public void start() throws Exception {
        // extract-code ignore adapter-s1
        installAssembly(new TutorialAssembly());
    }

}
