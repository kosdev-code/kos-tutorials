package com.kondra.device.mgmt;

import com.kosdev.kos.core.service.app.Application;
import com.kosdev.kos.core.service.app.BaseAppConfig;

public class DeviceManagementApplication extends Application<BaseAppConfig> {

    private TelemetryService telemetryService;


    @Override
    public void load() throws Exception {

        // add telemetry service to context
        addToCtx(telemetryService = new TelemetryService());

        // add controller to context
        addToCtx(new DeviceManagementController(telemetryService));

        // context is automatically updated after load
    }

    @Override
    public void start() throws Exception {

        // The whole app is up now we sync up with the server
        telemetryService.syncWithServer();
    }

    @Override
    public void started() throws Exception {

        // The app has officially started
    }

    @Override
    public void stop() throws Exception {
        // app is stopping a place to clean up resources
    }

    @Override
    public void unload() throws Exception {
        // app is being unloaded
    }


}
