package com.kondra.testing.app;

import com.tccc.kos.core.service.app.BaseAppConfig;
import com.tccc.kos.core.service.app.SystemApplication;
import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.hardware.IfaceAware;
import com.tccc.kos.core.service.log.BlinkLoggerIface;


public class TestApp extends SystemApplication<BaseAppConfig> {

    private BlinkLoggerIface loggerIface;

    @Override
    public void load() {
        addToCtx(new ArduinoController());
    }

    @Override
    public void start() {
        installAssembly(new TestAssembly("core"));
    }

    @Override
    public void started() {
    }
}
