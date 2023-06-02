/**
 * (C) Copyright 2023 TCCC. All rights reserved.
 */
package com.example;

import com.tccc.kos.core.service.app.AppConfig;
import com.tccc.kos.core.service.app.SystemApplication;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyKosApp extends SystemApplication<AppConfig> {

    @Getter
    private OurAssembly assembly;

    @Override
    public void load() {
        log.info("MyKosApp.load()");
        // Create the controller and add it to the kOS context:
        getCtx().add(new MyController());
    }

    @Override
    public void start() {
        log.info("MyKosApp.start()");
        assembly = new OurAssembly();
        installAssembly(assembly);
        log.info("Assembly installed.");
    }
}
