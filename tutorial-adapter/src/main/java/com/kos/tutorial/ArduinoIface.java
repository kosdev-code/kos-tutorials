package com.kos.tutorial;

import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsg;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgIface;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArduinoIface extends BinaryMsgIface {
    private static final String IFACE_NAME = "arduino.iface";

    private static final int API_ILLUMINATE_LED = 0;
    private static final int API_BUTTON_PRESSED = 1;

    private boolean isOn = false;

    public ArduinoIface(BinaryMsgSession session) {
        super(IFACE_NAME, session, null);

        this.addRequestHandler(API_BUTTON_PRESSED, res -> {
            BinaryMsg msg = msg(API_ILLUMINATE_LED);
            // If the led is on turn it off otherwise turn it on
            msg.writeBoolean(isOn = !isOn);
            send(msg);
            log.info("Button pressed turning led {}", isOn ? "On" : "Off");

        });
    }

}
