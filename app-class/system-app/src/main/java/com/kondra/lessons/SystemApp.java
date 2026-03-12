package com.kondra.lessons;

import com.kondra.device.mgmt.DeviceManagementApplication;
import com.kondra.device.mgmt.data.DeviceManagementInfo;
import com.kondra.lessons.ui.Frontend;
import com.kondra.lessons.ui.FrontendController;
import com.kosdev.kos.commons.core.context.annotations.Autowired;
import com.kosdev.kos.commons.web.api.ApiClient;
import com.kosdev.kos.core.service.app.BaseAppConfig;
import com.kosdev.kos.core.service.app.SystemApplication;

public class SystemApp extends SystemApplication<BaseAppConfig> {

    @Autowired
    private ApiClient apiClient;
    private DataGenerator dataGenerator;
    private FrontendController frontendController;

    @Override
    public void load() throws Exception {
        Frontend frontend = new Frontend();

        addToCtx(dataGenerator = new DataGenerator());
        addToCtx(frontendController = new FrontendController(frontend));

    }

    @Override
    public void start() throws Exception {
        // when the device management app is started, get the device info
        whenAppStarted(DeviceManagementApplication.APP_ID, app -> {

            // can safely call endpoint on app that is started
            // get device info from device management app thats running on the primary node
            DeviceManagementInfo info = apiClient.primaryGet("api/kos/app/device-management/info",
                    DeviceManagementInfo.class).getData();

            frontendController.setDeviceManagementInfo(info);

        });
    }

    @Override
    public void started() throws Exception {
        dataGenerator.start();
    }

}
