package com.kondra.device.mgmt;

import com.kondra.device.mgmt.service.ApiService;
import com.kondra.device.mgmt.service.DeviceManagementController;
import com.kondra.device.mgmt.service.HeartBeatService;
import com.kondra.device.mgmt.service.TelemetryService;
import com.kosdev.kos.core.service.app.Application;
import com.kosdev.kos.core.service.app.BaseAppConfig;
import com.kosdev.kos.sdk.annotations.Sdk;

import lombok.Getter;

@Sdk
public class DeviceManagementApplication extends Application<BaseAppConfig> {

    public static final String APP_ID = "kondra.device-management";

    private TelemetryService telemetryService;
    @Getter
    private ApiService apiService;


    @Sdk.Exclude
    @Override
    public void load() throws Exception {

        // add telemetry service to context
        addToCtx(telemetryService = new TelemetryService());

        // add controller to context
        addToCtx(new DeviceManagementController(telemetryService));
        addToCtx(new HeartBeatService());
        addToCtx(apiService = new ApiService());

        // context is automatically updated after load
    }


    @Sdk.Exclude
    @Override
    public void start() throws Exception {

        // The whole app is up now we sync up with the server
        telemetryService.syncWithServer();
    }

    @Sdk.Exclude
    @Override
    public void started() throws Exception {

        // The app has officially started
    }


    @Sdk.Exclude
    @Override
    public void stop() throws Exception {
        // app is stopping a place to clean up resources
    }


    @Sdk.Exclude
    @Override
    public void unload() throws Exception {
        // app is being unloaded
    }


}
