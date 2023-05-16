/**
 * (C) Copyright 2023 TCCC. All rights reserved.
 */
package com.tccc.kos.tutorials.helloworld;

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
}
