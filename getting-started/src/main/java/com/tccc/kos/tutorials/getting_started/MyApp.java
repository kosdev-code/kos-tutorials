/**
 * (C) Copyright 2023 TCCC. All rights reserved.
 */
package com.tccc.kos.tutorials.getting_started;

import com.tccc.kos.commons.core.service.config.ConfigBean;
import com.tccc.kos.core.service.app.SystemApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyApp extends SystemApplication {

    @Override
    public void load() {
        log.info("MyApp.load()");
    }

    @Override
    public void start() {
        log.info("MyApp.start()");
    }

    @Override
    public void stop() {
        log.info("MyApp.stop()");
    }

    @Override
    public void unload() {
        log.info("MyApp.unload()");
    }

    @Override
    public void setConfig(ConfigBean configBean) {
        log.info("MyApp.setConfig()");
    }
}
