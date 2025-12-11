/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsg;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgIface;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgIfaceListener;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;

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
    // API number for setting the thermostat operating mode
    private static final int API_SET_MODE = 1;

    public ThermostatIface(BinaryMsgSession session, BinaryMsgIfaceListener<?> listener) {
        super(NAME, session, listener);
    }

    /**
     * Return the temperature reading in Fahrenheit.
     */
    public double getTemp() throws IOException {
        BinaryMsg msg = msg(API_GET_TEMP);
        return sendAndRecvInt(msg);
    }

    /**
     * Set the operating mode of the thermostat.
     */
    public void setMode(Mode mode) throws IOException {
        BinaryMsg msg = msg(API_SET_MODE);
        msg.writeByte(mode.ordinal());
        send(msg);
    }
}
