/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.service.blink.binarymsg.*;

import java.io.IOException;

/**
 * Binary message interface for a simple thermostat device.
 * <p>
 * {@code ThermostatIface} defines the binary protocol used to communicate
 * with a thermostat over a {@link BinaryMsgSession}. Each public method
 * corresponds to a firmware API command identified by a small integer
 * (the API number).
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
public class ThermostatIface extends BinaryMsgIface {
    // iface name
    public static final String NAME = "kos.tutorial.thermostat";

    // API numbers
    // API number for requesting current temperature
    private static final int API_GET_TEMP = 0;
    // API number for requesting current mode
    private static final int API_GET_MODE = 1;
    // API number for setting the thermostat operating mode
    private static final int API_SET_MODE = 2;

    public ThermostatIface(BinaryMsgSession session, IfaceClient client) {
        super(NAME, session, client, null);
    }

    /**
     * Return the temperature reading in Fahrenheit.
     */
    public int getTemp() throws IOException {
        BinaryMsg msg = msg(API_GET_TEMP);
        return sendAndRecvInt(msg);
    }

    public Mode getMode() throws IOException {
        BinaryMsg msg = msg(API_GET_MODE);
        int value = sendAndRecvInt(msg);
        return Mode.values()[value];
    }

    /**
     * Set the operating mode of the thermostat.
     */
    public void setMode(Mode mode) throws IOException {
        BinaryMsg msg = msg(API_SET_MODE);
        msg.writeInt(mode.ordinal());
        send(msg);
    }
}
