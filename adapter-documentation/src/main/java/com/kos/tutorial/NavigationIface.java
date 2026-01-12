package com.kos.tutorial;

import java.io.IOException;

import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsg;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgIface;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgIfaceListener;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NavigationIface extends BinaryMsgIface {
    //@formatter:off

    private static final String IFACE_NAME = "nasa.navigationModuleIface";

    // Java Api numbers
    private static final int API_JAVA_SEND              = 0; // java sends a command / event 
    private static final int API_JAVA_SEND_AND_RECEIVE  = 1; // java sends a command / event and recieves a response
    private static final int API_JAVA_SEND_STRUCT       = 2; // java sends structured data

    // Native Api numbers
    private static final int API_NATIVE_SEND            = 0; // native sends an event to java
    //@formatter:on

    private boolean isOn = false;

    public NavigationIface(BinaryMsgSession session, BinaryMsgIfaceListener<NavigationIface> listener) {
        super(IFACE_NAME, session, listener);

        // Register listeners for events from adapter
        this.addRequestHandler(API_NATIVE_SEND, res -> {
            int severity = res.readInt();

            log.info("Recieved event from adapter with severity {}", severity);
        });
    }

    private void handleButtonPressed() throws IOException {
        BinaryMsg msg = msg(API_ILLUMINATE_LED);
        // If the led is on turn it off otherwise turn it on
        msg.writeBoolean(isOn = !isOn);
        send(msg);
        log.info("Button pressed turning led {}", isOn ? "On" : "Off");
    }

}
