package com.kos.tutorial;

import com.kosdev.kos.commons.core.service.AbstractService;
import com.kosdev.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.kosdev.kos.commons.core.service.blink.binarymsg.IfaceClient;
import com.kosdev.kos.core.util.iface.IfaceAwareService;

public class ArduinoService extends AbstractService implements IfaceAwareService<ArduinoIface> {

    private static final String IFACE_NAME = "kondra.exampleIface";

    IfaceClient<ArduinoIface> client;

    public ArduinoService() {
        client = new IfaceClient<>();
    }


    @Override
    public ArduinoIface createIface(BinaryMsgSession session) {
        return new ArduinoIface(session, client);
    }

    @Override
    public String getIfaceName() {
        return IFACE_NAME;
    }

}
