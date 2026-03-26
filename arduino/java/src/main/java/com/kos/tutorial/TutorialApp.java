package com.kos.tutorial;


import com.kosdev.kos.core.service.app.BaseAppConfig;
import com.kosdev.kos.core.service.app.SystemApplication;

public class TutorialApp extends SystemApplication<BaseAppConfig> {

    @Override
    public void load() {
        addToCtx(new ArduinoController());
    }

    @Override
    public void start() {
        installAssembly(new TutorialAssembly("core"));
    }

    @Override
    public void started() {
    }
}
