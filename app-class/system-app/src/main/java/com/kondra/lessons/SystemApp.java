package com.kondra.lessons;

import com.kosdev.kos.commons.core.context.annotations.Autowired;
import com.kosdev.kos.commons.web.api.ApiClient;
import com.kosdev.kos.core.service.app.BaseAppConfig;
import com.kosdev.kos.core.service.app.SystemApplication;

public class SystemApp extends SystemApplication<BaseAppConfig> {

    private static final String DEVICE_MGMT_APP_ID = "kondra-device-management";
    private DataGenerator dataGenerator;
    @Autowired
    private ApiClient apiClient;

    @Override
    public void load() throws Exception {

        addToCtx(dataGenerator = new DataGenerator());

    }

    @Override
    public void start() throws Exception {
        // when the device management app is started, get the device info
        whenAppStarted(DEVICE_MGMT_APP_ID, app -> {

            // get device info from device management app thats runnign on the primary node
            apiClient.primaryGet("api/kos/app/device-management/info", DeviceManagementInfo.class);

        } );
    }

    @Override
    public void started() throws Exception {
        dataGenerator.start();
    }









}




