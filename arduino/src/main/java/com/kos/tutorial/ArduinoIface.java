package com.kos.arduino.tutorial;

import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsg;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgIface;
import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArduinoIface extends BinaryMsgIface {

    // IFACE NAME
    public final static String NAME = "kondra.arduino";

    // API NUMBERS :
    private final static int API_HANDLER_0 = 0;
    private final static int API_HANDLER_1 = 1;
    private final static int API_HANDLER_2 = 2;
    private final static int API_HANDLER_3 = 3;
    private final static int API_HANDLER_4 = 4;
    private final static int API_HANDLER_5 = 5;

    // API EVENTS:
    public final static int EVENT_SAMPLE = 6;

    public ArduinoIface(BinaryMsgSession session) {
        super(NAME, session, null);
        log.info("arduino iface created");

        // add the request handler for the sample event
        this.addRequestHandler(ArduinoIface.EVENT_SAMPLE, m -> {
            log.info("Sample event received: {}", m.readCString());
        });
    }

    /**
     * demonstrate sending a single type of information
     */
    public void hitHandler0() {
       log.info("hitHandler0 in iface");

        String sampleMessage = "Hello World!";

        try {
            // create the binary message and write the relevant information to it
            BinaryMsg msg = msg(API_HANDLER_0);
            msg.writeCString(sampleMessage);

            // calling send() will work because we are not expecting information to it
            send(msg);
        }catch(Exception e){
            log.info("failed to call handler 2", e);
        }
    }

    /**
     * demonstrate sending multiple types of information at the
     * same time
     */
    public void hitHandler1() {
        String sampleString1 = "Hello";
        String sampleString2 = "World!";
        int sampleNumber = 256;

        try {
            BinaryMsg msg = msg(API_HANDLER_1);

            msg.writeInt(sampleString1.length() + 1);
            msg.writeCString(sampleString1);
            msg.writeInt(sampleNumber);
            msg.writeCString(sampleString2);

            // calling send() will work because we are not expecting information to it
            send(msg);
        }catch(Exception e){
            log.info("failed to call handler 2", e);
        }
    }

    /**
     * demonstrate sending and receiving a single type of information
     */
    public void hitHandler2() {
        try {
            BinaryMsg msg = msg(API_HANDLER_2);
            BinaryMsg response = sendAndRecv(msg);

            //log what we received to demonstrate that we received it
            log.info("successfully received: {}", response.readCString());
        }catch(Exception e){
            log.info("failed to call handler 3", e);
        }
    }

    /**
     * demonstrate sending and receiving multiple types of information
     */
    public void hitHandler3() {
        try {
            BinaryMsg msg = msg(API_HANDLER_3);
            BinaryMsg response = sendAndRecv(msg);

            //log what we received to demonstrate that we received it
            log.info("successfully received: {}", response.readCString());
            log.info("successfully received: {}", response.readCString());
            log.info("successfully received: {}", response.readInt());
        }catch(Exception e){
            log.info("failed to call handler 3", e);
        }
    }

    /**
     * demonstrate the creation and handling of events
     */
    public void hitHandler4() {
        try {
            BinaryMsg msg = msg(API_HANDLER_4);
            send(msg);
        }catch(Exception e){
            log.info("failed to call handler 4", e);
        }
    }

    /**
     * demonstrate the creation of logs
     */
    public void hitHandler5() {
        try {
            BinaryMsg msg = msg(API_HANDLER_5);
            send(msg);
        }catch(Exception e){
            log.info("failed to call handler 5", e);
        }
    }
}
