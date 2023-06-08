package com.example;

import com.tccc.kos.core.service.app.AppConfig;
import com.tccc.kos.core.service.app.SystemApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyKosApp extends SystemApplication<AppConfig> {

    @Override
    public void load() {
        log.info("MyKosApp.load()");
    }

    @Override
    public void start() {
        log.info("MyKosApp.start()");
    }
}
