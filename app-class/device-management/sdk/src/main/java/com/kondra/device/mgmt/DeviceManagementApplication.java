package com.kondra.device.mgmt;

import com.kondra.device.mgmt.service.ApiService;
import com.kosdev.kos.core.service.app.Application;
import com.kosdev.kos.core.service.app.BaseAppConfig;

public class DeviceManagementApplication extends Application<BaseAppConfig> {

    public static final String APP_ID = "kondra.device-management";

    public ApiService getApiService() {
        return null;
    }
}
