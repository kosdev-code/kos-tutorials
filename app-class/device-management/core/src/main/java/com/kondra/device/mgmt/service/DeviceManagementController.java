package com.kondra.device.mgmt.service;

import com.kondra.device.mgmt.data.DeviceManagementInfo;
import com.kosdev.kos.commons.core.context.annotations.Autowired;
import com.kosdev.kos.commons.core.dispatcher.annotations.ApiController;
import com.kosdev.kos.commons.core.dispatcher.annotations.ApiEndpoint;

@ApiController(base = "/device-managemant", title = "Device Management")
public class DeviceManagementController {

    @Autowired
    private TelemetryService telemetryService;

    @ApiEndpoint(GET = "/info", 
                desc = "This endpoint will try to send information" +
                       " down to the adapter through the baord")
    public DeviceManagementInfo getDeviceManagementInfo() {
        return telemetryService.getDeviceManagementInfo();
    }
}
