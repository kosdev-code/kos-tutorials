/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.hardware.Board;
import com.tccc.kos.core.service.hardware.IfaceAwareBoard;

import java.io.IOException;

/**
 * This is the logical abstraction of the physical thermostat board
 * The thermostat board is an arduino with sensors
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-10
 */
public class ThermostatBoard extends Board implements IfaceAwareBoard<ThermostatIface> {
    // board type
    private static final String TYPE = "kos.tutorial.thermostat";
    // instance ID
    private static final String INSTANCE_ID = "1";

    public ThermostatBoard(Assembly assembly, String name) {
        // Create a constructor matching the super Board constructor
        super(assembly, name);
    }

    /**
     * Retrieves the current ambient temperature from the physical
     * temperature sensor in the environment.
     */
    public double getTemp() throws IOException {
        if (getIface() == null) {
            throw new IOException();
        }
        return getIface().getTemp();
    }

    /**
     * Sets the operating mode of the physical thermostat, such as
     * heating, cooling, or idle (no active temperature control).
     */
    public void setMode(Mode mode) {
        withIface(iface -> iface.setMode(mode) );
    }

    public Mode getMode() throws IOException {
        if (getIface() == null) {
            throw new IOException();
        }
        return getIface().getMode();
    }

    @Override
    public String getType() {
        // Board type must match the arduino side
        // By convention, board type and board name are the same
        return TYPE;
    }

    @Override
    public String getInstanceId() {
        // By convention, instance ids are numbered. Since we are only using a single physical
        // arduino in this tutorial, our instance id will be "1"
        return INSTANCE_ID;
    }

    @Override
    public ThermostatIface createIface(BinaryMsgSession session) {
        return new ThermostatIface(session, this);
    }

    @Override
    public void onIfaceConnect() throws Exception {}

    @Override
    public void onIfaceDisconnect() throws Exception {}
}
