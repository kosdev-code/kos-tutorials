package com.kondra.lessons.ui;

import com.kondra.device.mgmt.data.DeviceManagementInfo;
import com.kosdev.kos.commons.core.context.annotations.Autowired;
import com.kosdev.kos.commons.util.ready.ReadyAndReadyListener;
import com.kosdev.kos.commons.util.ready.ReadyIndicator;
import com.kosdev.kos.commons.web.broker.BrokerClient;

/**
 * This controller isn't the typical controller used in KOS 
 * with endpoints this is more following the MVC pattern
 * where business logic is in the controller and the 
 * UI / View is in its own class.
 */
public class FrontendController implements ReadyAndReadyListener {

    @Autowired
    private BrokerClient brokerClient;

    private final Frontend frontend;

    public FrontendController(Frontend frontend) {
        this.frontend = frontend;
    }


    public void setDeviceManagementInfo(DeviceManagementInfo info) {
        frontend.setDeviceManagementInfo(info);
    }


    @Override
    public ReadyIndicator getReady() {
        ReadyIndicator ready = new ReadyIndicator();
        ready.isReady();
        return ready;
    }

    @Override
    public boolean onBeanReady() {
        brokerClient.localSubscribe("heartbeat", null, (data, x) -> frontend.showHeartbeat());
        return true;
    }

}
