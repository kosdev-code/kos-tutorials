package com.kos.tutorial;

import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.kab.KabFile;
import com.tccc.kos.core.service.app.BaseAppConfig;
import com.tccc.kos.core.service.app.SystemApplication;
import com.tccc.kos.core.service.fuse.FuseMount;
import com.tccc.kos.core.service.fuse.FuseService;
import com.tccc.kos.core.service.spawn.SpawnService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TutorialApp extends SystemApplication<BaseAppConfig> {

    @Autowired 
    private SpawnService spawnService;
    @Autowired
    private FuseService fuseService;
    private ExampleAdapter adapter;

    @Override
    public void load() throws Exception {
        // Get the adapter from the image and mount it so 
        // that it can be run on the device
        KabFile adapterKab = getKabByType("kos.adapter");
        if (adapterKab != null) {
            FuseMount mount = fuseService.mount(adapterKab);
            if (mount != null) {
                adapter = new ExampleAdapter(mount.getRootDir());
            }
        } else {
            log.error("No adapter found");
        }

    }

    @Override
    public void start() throws Exception {
        // install assembly after everything is setup in load
        installAssembly(new TutorialAssembly());

        // Spawn the adapter
        spawnService.addProcess(adapter);
    }

}
