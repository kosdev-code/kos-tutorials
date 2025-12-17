/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.client.binarymsg.BinaryMsgClient;
import com.tccc.kos.commons.core.client.binarymsg.BinaryMsgClientIface;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgIdentity;

/**
 * This is the thermostat client which communicates with the BinaryMsgIface on the thermostat app side
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
public class ThermostatClient extends BinaryMsgClient {
    private static final String NAME = "kos.tutorial.simulator";

    // API numbers for the protocol
    // API number for requesting current temperature
    private static final int API_GET_TEMP = 0;
    // API number for requesting current mode
    private static final int API_GET_MODE = 1;
    // API number for setting the thermostat operating mode
    private static final int API_SET_MODE = 2;

    public ThermostatClient(Thermostat thermostat, BinaryMsgIdentity identity) {
        super(NAME, identity, "localhost", 10001);
        // add handlers to the core iface
        BinaryMsgClientIface iface = getCoreIface();
        iface.addHandler(API_GET_TEMP, (api, msg) -> msg.writeDouble((thermostat.getTemp())));
        iface.addHandler(API_GET_MODE, (api, msg) -> msg.writeDouble((thermostat.getMode())));
        iface.addHandler(API_SET_MODE, (api, msg) ->thermostat.setMode(msg.readInt()));

        // Board Iface
//        BinaryMsgClientIface boardIface = new BinaryMsgClientIface();
//        this.addIface(boardIface);
    }
}
