package com.kondra.testing.app;

import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsg;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgIface;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ArduinoIface extends BinaryMsgIface {

    public final static String NAME = "kondra.arduino";

    private final static int API_TRIGGER_LOG = 0;

    public ArduinoIface(BinaryMsgSession session) {
        super(NAME, session, null);
        log.info("arduino iface created");
    }

    public void triggerLog() {
        try {
            log.info("triggering log");

            BinaryMsg msg = msg(API_TRIGGER_LOG);
            send(msg);
        }catch(Exception e){
            log.info("failed to trigger log");
        }
    }
}
