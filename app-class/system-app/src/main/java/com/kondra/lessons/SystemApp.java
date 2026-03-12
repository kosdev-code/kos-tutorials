package com.kondra.lessons;

import com.kondra.device.mgmt.DeviceManagementApplication;
import com.kondra.device.mgmt.data.DeviceManagementInfo;
import com.kosdev.kos.commons.core.context.annotations.Autowired;
import com.kosdev.kos.commons.web.api.ApiClient;
import com.kosdev.kos.core.service.app.BaseAppConfig;
import com.kosdev.kos.core.service.app.SystemApplication;

public class SystemApp extends SystemApplication<BaseAppConfig> {

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
        whenAppStarted(DeviceManagementApplication.APP_ID, app -> {

            // can safely call endpoint on app that is started
            // get device info from device management app thats running on the primary node
            DeviceManagementInfo info = apiClient.primaryGet("api/kos/app/device-management/info",
                    DeviceManagementInfo.class).getData();

        });
    }

    @Override
    public void started() throws Exception {
        dataGenerator.start();
    }

}
