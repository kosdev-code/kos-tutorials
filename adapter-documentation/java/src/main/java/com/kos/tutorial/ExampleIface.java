package com.kos.tutorial;

import java.io.IOException;

import com.kosdev.kos.commons.core.service.blink.binarymsg.BinaryMsg;
import com.kosdev.kos.commons.core.service.blink.binarymsg.BinaryMsgIface;
import com.kosdev.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.kosdev.kos.commons.core.service.blink.binarymsg.IfaceClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExampleIface extends BinaryMsgIface {
    //@formatter:off

    private static final String IFACE_NAME = "kondra.exampleIface";

    // Java Api numbers
    private static final int API_JAVA_SEND              = 0; // java sends a command / event 
    private static final int API_JAVA_SEND_AND_RECEIVE  = 1; // java sends a command / event and receives a response
    private static final int API_JAVA_SEND_STRUCT       = 2; // java sends structured data

    // Native Api numbers
    private static final int API_NATIVE_SEND            = 0; // native sends an event to java
    //@formatter:on

    public ExampleIface(BinaryMsgSession session, IfaceClient<ExampleIface> ifaceClient ) {
        super(IFACE_NAME, session, ifaceClient, null);

        // Register listeners for events from adapter
        this.addRequestHandler(API_NATIVE_SEND, res -> {
            int severity = res.readInt();
            log.info("Recieved event from adapter with severity {}", severity);
        });
    }

    public void resetModule(boolean isHardReset) throws IOException {
        BinaryMsg msg = msg(API_JAVA_SEND);

        // If the led is on turn it off otherwise turn it on
        msg.writeBoolean(isHardReset);
        send(msg);
    }

    /**
     *
     * @return an array representing the current coordinates in the form of [x,y,z]
     */
    public double[] getCurrentCoordinates() throws IOException {
        double[] coords = new double[3];

        BinaryMsg request = msg(API_JAVA_SEND_AND_RECEIVE);

        BinaryMsg response = sendAndRecv(request);
        coords[0] = response.readDouble();
        coords[1] = response.readDouble();
        coords[2] = response.readDouble();

        return coords;
    }

    /**
     * Send structured data to the adapter
     * 
     * @returns true if the call was successful false otherwise
     */
    public boolean setDestination(double x, double y, double z, String name) throws IOException {
        BinaryMsg req = msg(API_JAVA_SEND_STRUCT);
        req.writeDouble(x);
        req.writeDouble(y);
        req.writeDouble(z);
        req.writeCString(name);
        BinaryMsg res = sendAndRecv(req);

        boolean success = res.readBoolean();
        log.info("Setting destination {}", success ? "succeeded" : "failed");

        return success;
    }

}
