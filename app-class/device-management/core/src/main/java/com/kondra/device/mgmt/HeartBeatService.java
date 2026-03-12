package com.kondra.device.mgmt;

import com.kosdev.kos.commons.core.broker.MessageBroker;
import com.kosdev.kos.commons.core.context.annotations.Autowired;
import com.kosdev.kos.commons.core.service.AbstractConfigurableService;
import com.kosdev.kos.commons.core.service.config.BeanChanges;
import com.kosdev.kos.commons.util.concurrent.AdjustableCallback;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartBeatService extends AbstractConfigurableService<HeartBeatServiceConfig> {

    @Autowired
    private MessageBroker broker;

    private AdjustableCallback heartBeatCallback;

    @Override
    public boolean onBeanReady() {
        heartBeatCallback = new AdjustableCallback(true, getConfig().getHearBeatInterval(), () -> {
            log.info("Sending hearbeat");
            broker.send("heartbeat");

        });
        return true;
    }

    @Override
    public void onConfigChanged(BeanChanges changes) {
        heartBeatCallback.setAbsDelay(getConfig().getHearBeatInterval());
    }
}
