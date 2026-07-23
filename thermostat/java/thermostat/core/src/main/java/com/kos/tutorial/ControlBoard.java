/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.kosdev.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.kosdev.kos.commons.core.service.blink.binarymsg.IfaceClient;
import com.kosdev.kos.core.service.assembly.Assembly;
import com.kosdev.kos.core.service.hardware.Board;
import com.kosdev.kos.core.service.hardware.IfaceAwareBoard;

/**
 * This is the logical abstraction of the physical thermostat board
 * The thermostat physical board is an arduino with sensors
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
// extract-code thermostat-setup-board
public class ControlBoard extends Board implements IfaceAwareBoard {
    // board type: ensure it is unique by namespacing
    private static final String TYPE = "kos.tutorial.thermostat";
    // instance ID
    private static final String INSTANCE_ID = "1";
    // extract-code ignore thermostat-setup-board
    // Client to safely call methods on the iface without error checking
    private final IfaceClient<ThermostatIface> client;

    public ControlBoard(Assembly assembly, String name) {
        // Create a constructor matching the super Board constructor
        super(assembly, name);
        // extract-code ignore thermostat-setup-board
        // Create an iface Client
        client = new IfaceClient<>();
    }

    // extract-code ignore thermostat-setup-board
    // extract-code thermostat-setup-s3
    // extract-code thermostat-backend-s1
    /**
     * Retrieves the current ambient temperature from the physical
     * temperature sensor in the environment.
     */
    public Integer getTemp() {
        // extract-code ignore thermostat-setup-s3
        return client.fromCatch(iface -> iface.getTemp(), null);
    }

    // extract-code ignore thermostat-setup-board
    public Mode getMode() {
        // extract-code ignore thermostat-setup-s3
        return client.fromCatch(iface -> iface.getMode(), null);
    }
    // extract-code end thermostat-backend-s1

    // extract-code ignore thermostat-setup-board
    /**
     * Sets the operating mode of the physical thermostat, such as
     * heating, cooling, or idle (no active temperature control).
     */
    // extract-code thermostat-backend-s2
    public void setMode(Mode mode) {
        // extract-code ignore thermostat-setup-s3
        client.withCatch(iface -> iface.setMode(mode));
    }
    // extract-code end thermostat-setup-s3

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

    // extract-code ignore thermostat-setup-board
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
