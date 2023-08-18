package com.example;

import com.tccc.kos.core.service.app.BaseAppConfig;
import com.tccc.kos.core.service.app.SystemApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyKosApp extends SystemApplication<BaseAppConfig> {

    @Override
    public void load() {
        log.info("MyKosApp.load()");
        getCtx().add(new MyController());
    }

    @Override
    public void start() {
        log.info("MyKosApp.start()");
    }

    /**
     * Used as a fake entry point required when debugging.
     */
    public static void main(String[] args) {
    }
}
