/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.tccc.kos.commons.core.service.blink.binarymsg.IfaceClient;
import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.hardware.Board;
import com.tccc.kos.core.service.hardware.IfaceAwareBoard;

/**
 * This is the logical abstraction of the physical thermostat board
 * The thermostat physical board is an arduino with sensors
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
public class ThermostatBoard extends Board implements IfaceAwareBoard {
    // board type
    private static final String TYPE = "kos.tutorial.thermostat";
    // instance ID
    private static final String INSTANCE_ID = "1";

    // Client to safely call methods on the iface without error checking
    private final IfaceClient<ThermostatIface> client;

    public ThermostatBoard(Assembly assembly, String name) {
        // Create a constructor matching the super Board constructor
        super(assembly, name);
        // Create an iface Client
        client = new IfaceClient<>();
    }

    /**
     * Retrieves the current ambient temperature from the physical
     * temperature sensor in the environment.
     */
    public Integer getTemp() {
        return client.fromCatch(iface -> iface.getTemp(), null);
    }

    /**
     * Sets the operating mode of the physical thermostat, such as
     * heating, cooling, or idle (no active temperature control).
     */
    public void setMode(Mode mode) {
        client.withCatch(iface -> iface.setMode(mode));
    }

    public Mode getMode() {
        return client.fromCatch(iface -> iface.getMode(), null);
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

    /**
     * Callback received when implementing IfaceAwareBoard on creating a link session
     * Bind an instance of the ThermostatIface to the session
     * This iface will be later bound to the IfaceClient, which is how the logical board
     * communicates to the physical board
     */
    @Override
    public void onLinkSession(BinaryMsgSession session) {
        session.bind(new ThermostatIface(session, client));
    }
}
