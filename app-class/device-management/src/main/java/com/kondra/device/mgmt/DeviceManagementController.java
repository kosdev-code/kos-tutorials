package com.kondra.device.mgmt;

import com.kondra.device.mgmt.data.DeviceManagementInfo;
import com.kosdev.kos.commons.core.dispatcher.annotations.ApiController;
import com.kosdev.kos.commons.core.dispatcher.annotations.ApiEndpoint;


@ApiController(base = "/kondra-dm", title = "Endpoints for system app to use, and customer support")
public class DeviceManagementController {


    // Get Specific Device managent data

    private TelemetryService telemetryService;

    public DeviceManagementController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }


    @ApiEndpoint(  GET =  "/info",
                   desc = "This endpoint will try to send information down to the adapter through the baord")
    public DeviceManagementInfo getDeviceManagementInfo() {
        return telemetryService.getDeviceManagementInfo();
    }




}

