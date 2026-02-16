package com.kos.tutorial;

import com.kosdev.kos.commons.core.service.AbstractService;
import com.kosdev.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.kosdev.kos.commons.core.service.blink.binarymsg.IfaceClient;
import com.kosdev.kos.core.util.iface.IfaceAwareService;

public class ExampleService extends AbstractService implements IfaceAwareService<ExampleIface> {

    private static final String IFACE_NAME = "kondra.exampleIface";

    IfaceClient<ExampleIface> client;

    public ExampleService() {
        client = new IfaceClient<>();
    }


    @Override
    public ExampleIface createIface(BinaryMsgSession session) {
        return new ExampleIface(session, client);
    }

    @Override
    public String getIfaceName() {
        return IFACE_NAME;
    }

}
