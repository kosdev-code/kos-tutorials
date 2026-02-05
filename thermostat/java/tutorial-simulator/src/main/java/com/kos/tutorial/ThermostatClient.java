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
    private static final String NAME = "kos.tutorial.thermostat";
    private static final String INSTANCE_ID = "1";
    private static final String BOARD_NAME = "kos.board";

    // api numbers for the Board Iface
    private static final int API_GET_TYPE           = 1;
    private static final int API_GET_INSTANCE_ID    = 2;
    private static final int API_GET_IDENTITY       = 3;
    private static final int API_GET_MFG_SERIAL_NUM = 4;

    // API numbers for the Core Iface
    // API number for requesting current temperature
    private static final int API_GET_TEMP = 0;
    // API number for setting the thermostat operating mode
    private static final int API_SET_MODE = 1;

    public ThermostatClient(Thermostat thermostat, BinaryMsgIdentity identity) {
        super(NAME, identity, "localhost", 10001);
        // Add handlers to the core iface: Thermostat Iface
        BinaryMsgClientIface iface = getCoreIface();
        iface.addHandler(API_GET_TEMP, (api, msg) -> msg.writeInt((int) (thermostat.getTemp())));
        iface.addHandler(API_SET_MODE, (api, msg) -> thermostat.setMode(msg.readInt()));

        // Add handlers to the Board Iface
        BinaryMsgClientIface boardIface = new BinaryMsgClientIface(BOARD_NAME, 0);
        boardIface.addHandler(API_GET_TYPE, (api, msg) -> msg.writeCString(NAME));
        boardIface.addHandler(API_GET_INSTANCE_ID, (api, msg) -> msg.writeCString(INSTANCE_ID));
        boardIface.addHandler(API_GET_IDENTITY, (api, msg) -> msg.writeCString("1"));
        boardIface.addHandler(API_GET_MFG_SERIAL_NUM, (api, msg) -> msg.writeCString("1"));
        this.addIface(boardIface);
    }
}
