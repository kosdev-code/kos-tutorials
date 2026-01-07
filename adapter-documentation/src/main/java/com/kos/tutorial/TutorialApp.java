package com.kos.tutorial;

import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.kab.KabFile;
import com.tccc.kos.core.service.app.BaseAppConfig;
import com.tccc.kos.core.service.app.SystemApplication;
import com.tccc.kos.core.service.fuse.FuseMount;
import com.tccc.kos.core.service.fuse.FuseService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TutorialApp extends SystemApplication<BaseAppConfig> {
    private static final String ADAPTER_NAME = "exampleAdapter";

    @Autowired 
    private SpawnService spawnService;
    @Autowired
    private FuseService fuseService;
    private String adapterBaseDir;

    @Override
    public void load() throws Exception {
        // Get the adapter from the image and mount it so 
        // that it can be run on the device
        KabFile adapter = getKabByType("kos.adapter");
        if (adapter != null) {
            FuseMount mount = fuseService.mount(adapter);
            if (mount != null) {
                baseDir = mount.getRootDir();
            }
        } else {
            log.error("No adapter found");
        }

    }

    @Override
    public void start() throws Exception {
        // install assembly after everything is setup in load
        installAssembly(new TutorialAssembly());

        //Spawn adapter to start running
        // build the adapter
        Adapter adapter = new Adapter(ADAPTER_NAME);
        adapter.setBasePath(baseDir);

        adapter.addArg("-v");

        spawnService.spawn(adapter);



    }

}
